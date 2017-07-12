package com.alier.com.androidtools.ui.imobile.dataservice;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;
import com.alier.com.androidtools.commons.Config;
import com.alier.com.androidtools.commons.utils.T;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Size2D;
import com.supermap.data.SpatialQueryMode;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.WorkspaceVersion;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGroup;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Layers;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapLoadedListener;
import com.supermap.mapping.MapView;
import com.supermap.services.DataDownloadService;
import com.supermap.services.FeatureSet;
import com.supermap.services.ResponseCallback;

import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @Title:DataService
 * @description:地图数据服务对接
 * @author:gavin_fool
 * @date:2016年11月28日下午4:38:59
 * @version:v1.0
 */
public class DataService extends BaseActivity implements OnClickListener, MapLoadedListener {
	private MapView m_mapView;
	private MapControl m_mapControl; // 地图显示控件

	private final String sdcard = Config.ROOT_PATH;
	private final String filepath = sdcard + "/SuperMap/cwmap/restmap/CWMap.smwu";
	private Workspace m_workspace = null;
	private Datasource m_datasource = null;
	// private String mUrl = "http://101.201.42.160:8090/iserver";
	// private String mUrlDatasets =
	// "http://101.201.42.160:8090/iserver/services/data-CWMap/rest/data/datasources/GRID_SYSDB/datasets/";
	private String mUrl = "http://101.201.42.160/:8090/iserver";
	private String mUrlDatasets = "http://101.201.42.160/:8090/iserver/services/data-CWMap/rest/data/datasources/GRID_SYSDB/datasets/";
	private String datasetName = null;
	private String datasourceName = "part";

	@Override
	public void init() {
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		Environment.initialization(this);
		setContentView(R.layout.dataservice);
		initViews();
	}

	private void initViews() {
		((Button) findViewById(R.id.Button01)).setOnClickListener(this);
		((Button) findViewById(R.id.Button02)).setOnClickListener(this);
		((Button) findViewById(R.id.Button03)).setOnClickListener(this);
		((Button) findViewById(R.id.Button04)).setOnClickListener(this);
		m_mapView = (MapView) this.findViewById(R.id.mapview);
	}

	@Override
	public void exec() {
		// openworkspace();
		openMyWorkSpace();
	}

	private void openMyWorkSpace() {
		// 设置工作空间参数
		m_workspace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setType(WorkspaceType.SMWU);
		info.setVersion(WorkspaceVersion.UGC70);
		info.setServer(filepath);
		boolean isOpen = m_workspace.open(info);
		if (!isOpen) {
			T.show(DataService.this, "打开工作空间失败", 3000);
			this.finish();
		}
		// 打开地图
		m_mapView = (MapView) this.findViewById(R.id.mapview);
		m_mapControl = m_mapView.getMapControl();
		m_mapControl.getMap().setWorkspace(m_workspace);
		m_mapControl.getMap().setMapLoadedListener(this);
		String mapName = m_workspace.getMaps().get(1);
		m_mapControl.getMap().open(mapName);
		m_mapControl.getMap().setScale(0.0005);
		int count = m_workspace.getMaps().getCount();
//		Layers osmLayers = m_mapControl.getMap().getLayers();
//		LayerGroup layerGroup = (LayerGroup) osmLayers.get(0);
//		Dataset dataset = osmLayers.get(0).getDataset();
//		dataset.getName();
//				
//		for (int i = 0; i < osmLayers.getCount(); i++) {
//			if (osmLayers.get(i).getName().substring(0, osmLayers.get(i).getName().indexOf("@")).trim()
//					.equals("P0101")) {
//				osmLayers.get(i).setVisible(true);
//				osmLayers.get(i).setMaxVisibleScale(0.02);
//				osmLayers.get(i).setMinVisibleScale(0.000001);
//			}
//		}
		m_mapControl.getMap().refresh();
	}

	private void openworkspace() {
		m_mapControl = m_mapView.getMapControl();
		m_workspace = new Workspace();
		m_mapControl.getMap().setWorkspace(m_workspace);
		DatasourceConnectionInfo m_info = new DatasourceConnectionInfo();
		m_info.setServer("http://101.201.42.160:8090/iserver/services/data-CWMap/wfs100");
		m_info.setEngineType(EngineType.OGC);
		m_info.setDriver("WFS");
		// m_info.setAlias("长春市区图");
		m_info.setAlias("mobile");
		m_datasource = m_workspace.getDatasources().open(m_info);
		DatasetVector vector = (DatasetVector) m_datasource.getDatasets().get(0);
		Layer layer = m_mapControl.getMap().getLayers().add(vector, true);
		// m_mapControl.getMap().setScale(0.0001);
		m_mapControl.getMap().refresh();

	}

	public void queryParameterTest(DatasetVector dataset) {

		// 设置查询参数
		QueryParameter parameter = new QueryParameter();
		parameter.setAttributeFilter("SmID>0");
		parameter.setCursorType(CursorType.STATIC);
		parameter.setSpatialQueryMode(SpatialQueryMode.CONTAIN);
		// 进行查询
		int counts = dataset.getFieldCount();
		Recordset recordset = dataset.getRecordset(false, CursorType.STATIC);
		if (null != recordset) {
			isloadlayer = false;
			int count = recordset.getFieldCount();
			int co = recordset.getRecordCount();
			// 依次关闭所有对象
			dataset.close();
			recordset.dispose();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button01:// 上传

			break;
		case R.id.Button02:// 下载
			downloadService();
			break;
		case R.id.Button03:// 更新
			updateService();
			break;
		case R.id.Button04:// 删除
			break;
		default:
			break;
		}
	}

	/** 上传 */
	private void uploadService() {

	}

	/** 下载 */
	private void downloadService() {
		if (m_datasource == null)
			return;
		final ProgressDialog dialog = new ProgressDialog(this);
		datasetName = "P0113";
		String mUrlDataSet = mUrlDatasets + datasetName;
		// 构造下载对象
		DataDownloadService dataDownloadService = new DataDownloadService(mUrl);
		dataDownloadService.setResponseCallback(new ResponseCallback() {

			@Override
			public void requestSuccess() {
				T.showLong(DataService.this, "请求成功");

			}

			@Override
			public void requestFailed(String arg0) {
				dialog.dismiss();
				T.showLong(DataService.this, "请求失败" + arg0);

			}

			@Override
			public void receiveResponse(FeatureSet arg0) {
				T.showLong(DataService.this, "接收请求结果");
			}

			@Override
			public void dataServiceFinished(String arg0) {
				T.showLong(DataService.this, "数据服务结束");
				dialog.dismiss();
				showDownloadDataset();
				m_mapControl.getMap().refresh();
			}
		});

		dialog.setMessage("下载中。。。。");
		dialog.show();
		dataDownloadService.downloadDataset(mUrlDataSet, m_datasource);
	}

	/**
	 * 更新服务
	 */
	private void updateService() {
		if (m_datasource == null)
			return;
		final ProgressDialog dialog = new ProgressDialog(this);
		datasetName = "P0113";
		String urlDataSet = mUrlDatasets + datasetName;
		DataDownloadService dataDownloadService = new DataDownloadService(mUrl);
		dataDownloadService.setResponseCallback(new ResponseCallback() {

			@Override
			public void requestSuccess() {
				dialog.dismiss();
				T.showLong(DataService.this, "数据集更新完成");
			}

			@Override
			public void requestFailed(String arg0) {
				dialog.dismiss();
				T.showLong(DataService.this, "数据集更新失败");
			}

			@Override
			public void receiveResponse(FeatureSet arg0) {

				T.showLong(DataService.this, "接受响应");
			}

			@Override
			public void dataServiceFinished(String arg0) {
				dialog.dismiss();
				T.showLong(DataService.this, "更新成功");
				showDownloadDataset();
				m_mapControl.getMap().refresh();
			}
		});
		dialog.setMessage("更新中。。。");
		dialog.show();
		// 执行更新操作
		dataDownloadService.updateDataset(urlDataSet, (DatasetVector) m_datasource.getDatasets().get(datasetName));
	}

	/**
	 * @Description:显示下载的数据集
	 */
	private void showDownloadDataset() {

	}

	private void modifiedService(int type) {
		// 获取关联数据集
		DatasetVector dataset = (DatasetVector) m_datasource.getDatasets().get(datasetName);
		if (null == dataset)
			return;
		Recordset recordset = dataset.getRecordset(false, CursorType.DYNAMIC);
		switch (type) {
		// 修改字段名称为”SMUSERID”的字段的字段值为20
		case 1:
			if (!recordset.isEmpty()) {
				while (!recordset.isEOF()) {
					recordset.edit();
					recordset.setFieldValue("SMUSERID", 20);
					recordset.update();
					recordset.moveNext();
				}
			}
			recordset.update();
			recordset.dispose();
			break;
		// 向记录集中添加2个点几何对象
		case 2:
			if (!recordset.isEmpty()) {
				GeoPoint point1 = new GeoPoint(7056.451, 34690.675);
				recordset.addNew(point1);
				recordset.update();
				recordset.moveNext();
				GeoPoint point2 = new GeoPoint(6254.780, 54220.603);
				recordset.addNew(point2);
				recordset.update();
			}
		default:

			break;
		}
	}

	@Override
	public void onMapLoaded() {
		new Thread(loadlayer).start();
	}

	private boolean isloadlayer = false;
	Runnable loadlayer = new Runnable() {

		@Override
		public void run() {
			while (isloadlayer) {
				DatasetVector datasetVector = (DatasetVector) m_datasource.getDatasets().get("P0103");
				if (null != datasetVector) {
					queryParameterTest(datasetVector);
				}
				Layer layer = m_mapControl.getMap().getLayers().add(datasetVector, true);
				layer.setVisible(true);
				layer.setMaxVisibleScale(0.02);
				layer.setMinVisibleScale(0.0001);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};
}
