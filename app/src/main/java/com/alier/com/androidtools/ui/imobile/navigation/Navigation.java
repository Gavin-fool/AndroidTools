package com.alier.com.androidtools.ui.imobile.navigation;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;
import com.alier.com.androidtools.commons.Config;
import com.alier.com.androidtools.commons.utils.T;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.DatasetVector;
import com.supermap.data.Environment;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviInfo;
import com.supermap.navi.NaviListener;
import com.supermap.navi.Navigation2;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年11月2日 上午11:25:32
 * @version 1.0
 */
public class Navigation extends BaseActivity {

	private Workspace m_workSpace = null;
	private MapView m_MapView = null;
	private MapControl m_MapControl = null;
	private Map m_Map = null;
	private Point2D startPoint = null;// 记录起始点
	private Point2D destPoint = null;// 记录结束点
	private boolean longPressEnable = false;
	private boolean setStartPoint = false;
	private boolean setDestPoint = false;
	private boolean isFindPath = false;
	private boolean m_ExitEnable = false;
	private boolean isPathShowed = true;
	private Button btn_startPoint = null;
	private Button btn_destPoint = null;
	private Button btn_pathAnalyst = null;
	private Button btn_startGuide = null;
	private Button btn_stopGuide = null;
	private Button btn_showPath = null;
	private Navigation2 m_Navigation2 = null;
	private View layout;

	private final String workSpacePath = Config.ROOT_PATH + "/SampleData/Navigation2Data/navi_beijing.smwu";

	@Override
	public void init() {
		Environment.setLicensePath(Config.MAP_LIC_PATH);
		Environment.setWebCacheDirectory(Config.ROOT_PATH + "/SuperMap/WebCahe/");
		Environment.initialization(this);
		setContentView(R.layout.navigation);
		boolean isOpen = isOpenWorkSpace();
		if (isOpen) {
			initViews();
			initNavigation();
			startDefaultNavi();
		}

	}

	@Override
	public void exec() {

	}

	private void initViews() {
		btn_startPoint = (Button) findViewById(R.id.btn_startPoint);
		btn_destPoint = (Button) findViewById(R.id.btn_destPoint);
		btn_startGuide = (Button) findViewById(R.id.btn_startSimuGuide);
		btn_stopGuide = (Button) findViewById(R.id.btn_stopGuide);
		btn_pathAnalyst = (Button) findViewById(R.id.btn_findPath);
		btn_showPath = (Button) findViewById(R.id.btn_showPath);
		((Button) findViewById(R.id.btn_changeView)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_cleanPath)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_startRealGuide)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_clean)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.btn_showCar)).setOnClickListener(onClickListener);

		btn_startPoint.setOnClickListener(onClickListener);
		btn_destPoint.setOnClickListener(onClickListener);
		btn_pathAnalyst.setOnClickListener(onClickListener);
		btn_startGuide.setOnClickListener(onClickListener);
		btn_stopGuide.setOnClickListener(onClickListener);
		btn_showPath.setOnClickListener(onClickListener);

		layout = findViewById(R.id.layout_btns);
	}

	private void initNavigation() {
		String networkDatasetName = "BuildNetwork";
		DatasetVector networkDataset = (DatasetVector) m_workSpace.getDatasources().get("beijing").getDatasets()
				.get(networkDatasetName);
		m_Navigation2 = m_MapControl.getNavigation2(); // 获取行业导航控件，只能通过此方法初始化m_Navigation2
		m_Navigation2.setPathVisible(true); // 设置分析所得路径可见
		m_Navigation2.setNetworkDataset(networkDataset); // 设置网络数据集
		m_Navigation2.loadModel(Config.ROOT_PATH + "/SampleData/Navigation2Data/netModel.snm");
		m_Navigation2.addNaviInfoListener(new NaviListener() {

			@Override
			public void onStopNavi() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartNavi() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPlayNaviMessage(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNaviInfoUpdate(NaviInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAdjustFailure() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAarrivedDestination() {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 启动默认设置的模拟导航
	 */
	private void startDefaultNavi() {
		startPoint = getStartPoint(null);
		destPoint = getDestPoint(null);
		routeAnalyze();
	}

	private void routeAnalyze() {
		if (null == startPoint || null == destPoint) {
			T.showShort(Navigation.this, "请先设置起始和终止位置");
		} else {
			m_Navigation2.setStartPoint(startPoint.getX(), startPoint.getY());
			m_Navigation2.setDestinationPoint(destPoint.getX(), destPoint.getY());
			m_Navigation2.setPathVisible(true);
			isPathShowed = true;
			isFindPath = m_Navigation2.routeAnalyst();
			if (isFindPath) {
				m_Map.refresh();
			} else {
				T.showShort(Navigation.this, "路径分析失败");
			}
		}
	}

	/**
	 * 打开工作空间
	 *
	 * @return true:打开成功 ； false:打开失败
	 */
	@SuppressWarnings("deprecation")
	private boolean isOpenWorkSpace() {
		m_workSpace = new Workspace();
		WorkspaceConnectionInfo info = new WorkspaceConnectionInfo();
		info.setServer(workSpacePath);
		info.setType(WorkspaceType.SMWU);
		boolean isOpen = m_workSpace.open(info);
		if (!isOpen) {
			T.showShort(Navigation.this, "打开工作空间失败");
			return false;
		}
		m_MapView = (MapView) this.findViewById(R.id.mapView);
		m_MapControl = m_MapView.getMapControl();
		m_Map = m_MapControl.getMap();
		m_Map.setWorkspace(m_workSpace);
		m_Map.open(m_workSpace.getMaps().get(0)); // open map
		m_Map.refresh();
		m_MapControl.setGestureDetector(new GestureDetector(Navigation.this, longTouchListener));
		return true;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_startPoint:
				longPressEnable = true;
				setStartPoint = true;
				T.showShort(Navigation.this, "请长按设置起始位置");
				break;
			case R.id.btn_destPoint:
				longPressEnable = true;
				setDestPoint = true;
				T.showShort(Navigation.this, "请长按设置终止位置");
				break;
			case R.id.btn_findPath:
				routeAnalyze();
				break;
			case R.id.btn_clean:
				clean();
				break;
			default:
				break;
			}

		}
	};
	/**
	 * 长按监听事件
	 */
	private SimpleOnGestureListener longTouchListener = new SimpleOnGestureListener() {
		@Override
		public void onLongPress(MotionEvent event) {
			if (!longPressEnable)
				return;
			if (setStartPoint)
				startPoint = getStartPoint(event);
			if (setDestPoint)
				destPoint = getDestPoint(event);

			longPressEnable = false;
			setStartPoint = false;
			setDestPoint = false;
		}

		// 地图漫游
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (m_Navigation2.isGuiding())
				m_Navigation2.enablePanOnGuide(true);
			return false;
		}
	};

	/**
	 * 停止导航、清除起点、终点、路径
	 */
	private void clean() {

		if (m_Navigation2.isGuiding())
			m_Navigation2.stopGuide(); // 停止正在进行的导航
		m_Navigation2.cleanPath(); // 清除路径，需在停止导航后进行，否则无效
		m_Navigation2.enablePanOnGuide(false);
		m_MapView.removeAllCallOut();
		m_Map.refresh();

		startPoint = null;
		destPoint = null;
		isFindPath = false;
		isPathShowed = false;
		btn_showPath.setText("显示路径");
	}

	/**
	 * 获取起始点
	 *
	 * @param event
	 * @return
	 */
	private Point2D getStartPoint(MotionEvent event) {
		int x = 0;
		int y = 0;
		if (null == event) {
			x = 50;
			y = 50;
		} else {
			x = (int) event.getX();
			y = (int) event.getY();
		}
		Point point = new Point(x, y);
		return getPoint(point, "startPoint", R.drawable.startpoint);
	}

	/**
	 * 获取终点
	 *
	 * @param event
	 * @return
	 */
	private Point2D getDestPoint(MotionEvent event) {
		int x = 0;
		int y = 0;
		if (event == null) {
			x = 200;
			y = 200;
		} else {
			x = (int) event.getX();
			y = (int) event.getY();
		}
		Point point = new Point(x, y);
		return getPoint(point, "destPoint", R.drawable.destpoint);
	}

	/**
	 * 将屏幕坐标转换成地图坐标
	 *
	 * @param point
	 *            屏幕坐标
	 * @param pointName
	 *            添加注记名称
	 * @param idDrawable
	 *            图标资源id
	 * @return
	 */
	private Point2D getPoint(Point point, final String pointName, final int idDrawable) {
		Point2D point2D = m_Map.pixelToMap(point);
		showPointByCallout(point2D, pointName, idDrawable);
		if (m_Map.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			PrjCoordSys srcprjCoordSys = m_Map.getPrjCoordSys();
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(point2D);
			PrjCoordSys desPrjCoordSys = new PrjCoordSys();
			desPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			CoordSysTranslator.convert(point2Ds, srcprjCoordSys, desPrjCoordSys, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			point2D = point2Ds.getItem(0);
		}
		return point2D;
	}

	/**
	 * 显示Callout
	 *
	 * @param point
	 * @param pointName
	 * @param idDrawable
	 */
	private void showPointByCallout(Point2D point, final String pointName, final int idDrawable) {
		CallOut callOut = new CallOut(this);
		callOut.setStyle(CalloutAlignment.BOTTOM);
		callOut.setCustomize(true);
		callOut.setLocation(point.getX(), point.getY());
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(idDrawable);
		callOut.setContentView(imageView);
		m_MapView.addCallout(callOut, pointName);
	}
}
