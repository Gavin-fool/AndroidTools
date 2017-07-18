package com.alier.com.androidtools.ui.imobile.netAnalyst;

import com.alier.com.androidtools.R;
import com.alier.com.commons.BaseActivity;
import com.alier.com.commons.utils.T;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.data.Color;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Environment;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Layer;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.TrackingLayer;

import android.app.ProgressDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年11月7日 上午8:48:20
 * @version 1.0
 */
public class FindPath extends BaseActivity implements OnClickListener {
	private Workspace mWorkspace = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;

	/**
	 * 路径分析
	 */
	private Button btnRoute = null;
	/**
	 * 设置起点
	 */
	private Button btnSetting = null;
	/**
	 * 清理结果
	 */
	private Button btnClean = null;
	/**
	 * 重新设置起终点
	 */
	private Button btnDsetting = null;

	// 操作过程中的状态改变
	private boolean bEndPointEnable = false;
	private boolean bAnalystEnable = false;
	private boolean bLongPressEnable = false;
	// 当进行路径分析后则不能修改起点终点
	private boolean bSettingEnable = true;

	/**
	 * 表示网络数据集
	 */
	private DatasetVector m_datasetLine;
	private Layer m_layerLine;
	/**
	 * 跟踪图层，
	 */
	private TrackingLayer m_trackingLayer;
	private Point2Ds m_Points = null;
	private static String m_datasetName = "sm_roadnet";
//	private static String m_datasetName = "RoadNet";

	private static String m_nodeID = "SmNodeID";
	private static String m_edgeID = "SmEdgeID";
	/**
	 * 交通网络分析类，该类用于提供路径分析、旅行商分析、服务区分析、多旅行商（物流配送）分析、最近设施查找和选址分区分析等交通网络分析的功能
	 */
	private TransportationAnalyst m_analyst;
	/**
	 * 交通网络分析返回结果
	 */
	private TransportationAnalystResult m_result;
	private final String sdcard = android.os.Environment.getExternalStorageDirectory().getAbsolutePath().toString();
	private RelativeLayout parentRelativeLayout;// 把父类activity和子类activity的view都add到这里

	@Override
	public void init() {
		// 初始化环境,设置许可路径
		Environment.setLicensePath(sdcard + "/SuperMap/license/");
		// 设置一些系统需要用到的路径
		Environment.initialization(this);

		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		viewGroup.removeAllViews();
		parentRelativeLayout = new RelativeLayout(this);
		parentRelativeLayout.setVerticalGravity((RelativeLayout.CENTER_VERTICAL));
		viewGroup.addView(parentRelativeLayout);
		LayoutInflater.from(this).inflate(R.layout.findpath, parentRelativeLayout, true);
		initUI();
	}

	@Override
	public void exec() {
		initialize();
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("交通网络分析对象载入中...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//加载网络模型
				load();
				dialog.dismiss();
			}
		}).start();
		
	}

	private void initUI() {
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapControl = mMapView.getMapControl();
		btnRoute = (Button) findViewById(R.id.analyst);
		btnRoute.setOnClickListener(this);
		btnSetting = (Button) findViewById(R.id.setting);
		btnSetting.setOnClickListener(this);
		btnClean = (Button) findViewById(R.id.btn_bufferClean);
		btnClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMapControl.getMap().getTrackingLayer().clear();
				mMapControl.getMap().refresh();
			}
		});
		btnDsetting = (Button) findViewById(R.id.Dsetting);
		btnDsetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bSettingEnable = true;
				bEndPointEnable = false;
				bAnalystEnable = false;
				bLongPressEnable = true;
				mMapView.removeAllCallOut();
				m_Points.clear();
				btnSetting.setText("设置起点");
				btnRoute.setText("路径分析");
			}
		});

		m_Points = new Point2Ds();
	}

	/**
	 * 打开网络数据集并初始化相应变量
	 */
	private void initialize() {
		mWorkspace = new Workspace();

		// 打开数据源,得到点线数据集
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		String dataServer = sdcard + "/cgt_data_support/mapData/GLNMAP.smwu";
//		String dataServer = sdcard + "/SuperMap/City/Changchun.smwu";
		info.setType(WorkspaceType.SMWU);
		info.setServer(dataServer);
		boolean isOpenWorkSpace = mWorkspace.open(info);
		if(!isOpenWorkSpace){
			T.showShort(this, "打开工作空间失败！");
		}
		Datasource datasource = mWorkspace.getDatasources().get("DCS_DATA");
//		Datasource datasource = mWorkspace.getDatasources().get("changchun");
		mMapControl.getMap().setWorkspace(mWorkspace);
		mMapControl.getMap().open(mWorkspace.getMaps().get(0));
		m_datasetLine = (DatasetVector) datasource.getDatasets().get(m_datasetName);
		m_trackingLayer = mMapControl.getMap().getTrackingLayer();

		// 加载线数据集并设置风格
		m_layerLine = mMapControl.getMap().getLayers().add(m_datasetLine, true);
		m_layerLine.setSelectable(false);
//		LayerSettingVector lineSetting = (LayerSettingVector) m_layerLine.getAdditionalSetting();
//		GeoStyle lineStyle = new GeoStyle();
//		lineStyle.setLineColor(new Color(0, 0, 255));
//		lineStyle.setLineWidth(0.1);
//		lineSetting.setStyle(lineStyle);

		mMapControl.getMap().viewEntire();
		mMapControl.getMap().refresh();

		// 设置手势委托
		mMapControl.setGestureDetector(new GestureDetector(mGestureListener));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.analyst:
			if(!bAnalystEnable){
				T.showShort(FindPath.this, "请先设置起点和终点");
				return;
			}
			
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("正在查询路径。。。");
			dialog.show();
			Thread thread = new Thread(new Runnable() {
				public void run() {
					//网络分析
					analyst();
					dialog.dismiss();
				}
			});
			thread.start();
			break;
		case R.id.setting:
			if(!bSettingEnable){
				T.showShort(this, "不能修改起点和终点!");
				bLongPressEnable = true;
				return;
			}
			if(bEndPointEnable){
				T.showShort(this, "长按添加终点!");
				bLongPressEnable = true;
				return;
			}
			T.showShort(this, "长按设置起点!");
			bLongPressEnable = true;
			break;
		default:
			break;
		}
	}
	
	private boolean analyst(){
		TransportationAnalystParameter parameter = new TransportationAnalystParameter();
		parameter.setWeightName("length");
		//设置最佳路径返回的结果
		parameter.setPoints(m_Points);
		parameter.setEdgesReturn(true);
		parameter.setNodesReturn(true);
		parameter.setPathGuidesReturn(true);
		parameter.setRoutesReturn(true);
		try {
			m_result = m_analyst.findPath(parameter, false);
		} catch (Exception e) {
			m_result = null;
		}
		if(null == m_result){
			toastInfo("分析失败");
			return false;
		}
		showResult();
		return true;
	}
	
	private void toastInfo(final String msg){
		runOnUiThread(new Runnable() {
			public void run() {
				T.showShort(FindPath.this, msg);
			}
		});
	}
	
	/**
	 * 显示结果
	 */
	public void showResult()
	{

		//删除原有结果
		int count = m_trackingLayer.getCount();
		for (int i = 0; i < count; i++)
		{
			int index = m_trackingLayer.indexOf("result");
			if (index != -1)
				m_trackingLayer.remove(index);
		}

		GeoLineM[] routes = m_result.getRoutes();
		
		if (routes == null) {
			return;
		}
		
		for (int i = 0; i < routes.length; i++)
		{
			GeoLineM geoLineM = routes[i];
			GeoStyle style = new GeoStyle();
			style.setLineColor(new Color(255, 80, 0));
			style.setLineWidth(1);
			geoLineM.setStyle(style);
			m_trackingLayer.add(geoLineM, "result");
		}
		
		mMapControl.getMap().refresh();
	}
	
	/**
	 * 加载环境设置对象
	 */
	public void load()
	{
		// 设置网络分析基本环境，这一步骤需要设置　分析权重、节点、弧段标识字段、容限
		TransportationAnalystSetting setting = new TransportationAnalystSetting();
		setting.setNetworkDataset(m_datasetLine);
		setting.setEdgeIDField(m_edgeID);
		setting.setNodeIDField(m_nodeID);
		setting.setFNodeIDField("SmFNode");
		setting.setTNodeIDField("SmTNode");
		
		setting.setNodeNameField("SMNODEID");
		setting.setEdgeNameField("ID");
	
		setting.setTolerance(100000);
		

		WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
		WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
		weightFieldInfo.setFTWeightField("smLength");
		weightFieldInfo.setTFWeightField("smLength");
		weightFieldInfo.setName("length");
		weightFieldInfos.add(weightFieldInfo);
		setting.setWeightFieldInfos(weightFieldInfos);
		

		//构造交通网络分析对象，加载环境设置对象
		m_analyst = new TransportationAnalyst();
		m_analyst.setAnalystSetting(setting);//设置交通网络分析环境设置对象
		m_analyst.load();//加载网络模型

	}

	private GestureDetector.SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener(){
		/**
		 * 长按监听事件
		 */
		public void onLongPress(MotionEvent e) {
			if(!bLongPressEnable){
				return;
			}
			int x = (int) e.getX();
			int y = (int) e.getY();
			Point2D pt = mMapControl.getMap().pixelToMap(new Point(x, y));
			CallOut callout = new CallOut(FindPath.this);
			callout.setStyle(CalloutAlignment.LEFT_BOTTOM);
			callout.setCustomize(true);
			callout.setLocation(pt.getX(),pt.getY());
			//当投影不是经纬坐标系时，则对起始点进行投影转换 
//			if(mMapControl.getMap().getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE){
//				Point2Ds points = new Point2Ds();
//				points.add(pt);
//				PrjCoordSys desPrjCoorSys = new PrjCoordSys();
//				desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
//				CoordSysTranslator.convert(points, mMapControl.getMap().getPrjCoordSys(), desPrjCoorSys, new CoordSysTransParameter(), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
//				pt = points.getItem(0);
//			}
			ImageView image = new ImageView(FindPath.this);
			//添加第一个点
			if(!bEndPointEnable){
				image.setBackgroundResource(R.drawable.startpoint);
				callout.setContentView(image);
				mMapView.addCallout(callout);
				m_Points.add(pt);//添加目标点
				bEndPointEnable = true;
				bLongPressEnable = false;
				btnSetting.setText("添加目标点");
				btnSetting.invalidate();
				return;
			}
			image.setBackgroundResource(R.drawable.despoint);
			callout.setContentView(image);
			mMapView.addCallout(callout);
			m_Points.add(pt);//添加目标点
			bAnalystEnable = true;
			btnSetting.setText("添加目标点");
			bLongPressEnable = false;
		};
	};
}
