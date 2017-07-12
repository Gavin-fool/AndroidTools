package com.alier.com.androidtools.ui.imobile.dataservice;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;
import com.alier.com.androidtools.commons.Config;
import com.alier.com.androidtools.commons.utils.T;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.QueryParameter;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.services.FeatureSet;
import com.supermap.services.QueryMode;
import com.supermap.services.QueryOption;
import com.supermap.services.QueryService;
import com.supermap.services.ResponseCallback;
import com.supermap.services.ServiceQueryParameter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年11月30日 下午2:56:46
 * @version 1.0
 */
public class DataServiceShow extends BaseActivity implements OnClickListener{

	private MapView m_MapView = null;
	private MapControl m_MapControl = null;
	private Workspace m_WorkSpace = null;
	/** 服务地址 */
	private String mUrl = "http://172.16.211.206:8090/iserver";
	/** 地图服务端地址 */
	private String mUrlMap = "http://172.16.211.206:8090/iserver/services/map-CWMap/rest/maps/CWMap";
	/** 服务端数据集地址 */
	private String mUrlDatasets = "http://172.16.211.206:8090/iserver/services/data-CWMap/rest/data/datasources/GRID_SYSDB/datasets/";
	/** 指定打开的数据集名称 */
	private String datasetName = null;
	private String datasourceName = "GRID_SYSDB";
	
	private Button button01,button02;

	@Override
	public void init() {
		Environment.setLicensePath(Config.ROOT_PATH + "/SuperMap/license/");
		Environment.setWebCacheDirectory(Config.ROOT_PATH + "/SuperMap/cache/");
		Environment.setTemporaryPath(Config.ROOT_PATH + "/SuperMap/temp");
		Environment.initialization(DataServiceShow.this);
		setContentView(R.layout.dataserviceshow);
		m_MapView = (MapView) this.findViewById(R.id.mapview);
		m_MapControl = m_MapView.getMapControl();
		button01 = (Button)this.findViewById(R.id.Button01);
		button02 = (Button)this.findViewById(R.id.Button02);
		button01.setOnClickListener(this);
		button02.setOnClickListener(this);
	}

	@Override
	public void exec() {
		openWorkSpace();
	}

	private void openWorkSpace() {
		m_WorkSpace = new Workspace();
		m_MapControl.getMap().setWorkspace(m_WorkSpace);
		DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
		connectionInfo.setServer(mUrlMap);
		connectionInfo.setEngineType(EngineType.Rest);
		connectionInfo.setAlias("chongwen");
		Datasource datasource = m_WorkSpace.getDatasources().open(connectionInfo);
		if(null != datasource){
			m_MapControl.getMap().getLayers().add(datasource.getDatasets().get(0), true);
			m_MapControl.getMap().setMaxScale(0.002);//设置地图最大显示比例尺
			m_MapControl.getMap().setMinScale(0.0000001);//设置地图最小显示比例尺
			m_MapControl.getMap().getLayers().getCount();
			m_MapControl.getMap().refresh();
		}else{
			T.showLong(this, "打开网络服务失败");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Button01:
			queryDataSetFormService();
			break;

		default:
			break;
		}
	}
	
	private void queryDataSetFormService(){
		QueryService queryService = new QueryService(mUrl);
		ServiceQueryParameter parameter = new ServiceQueryParameter();
		parameter.setQueryMapName("CWMap");
		parameter.setQueryServiceName("map-CWMap/rest");
		parameter.setQueryLayerName("P0402@GRID_SYSDB");
		parameter.setQueryRecordStart(0);
		parameter.setQueryOption(QueryOption.ATTRIBUTEANDGEOMETRY);
		String filter = "SMID>0";
		parameter.setAttributeFilter(filter);
		queryService.setResponseCallback(new ResponseCallback() {
			
			@Override
			public void requestSuccess() {
				T.showLong(DataServiceShow.this, "查询成功");
			}
			
			@Override
			public void requestFailed(String arg0) {
				T.showLong(DataServiceShow.this, arg0.toString());
				
			}
			
			@Override
			public void receiveResponse(FeatureSet arg0) {
				T.showLong(DataServiceShow.this, "查询成功");
				
			}
			
			@Override
			public void dataServiceFinished(String arg0) {
				T.showLong(DataServiceShow.this, arg0.toString());
			}
		});
		queryService.query(parameter, QueryMode.SqlQuery);
	}
}
