package com.alier.com.androidtools.ui.map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.Config;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoStyle;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.GeometryAddedListener;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;

import java.util.Map;

/**
 * imobile地图基类，负责地图加载和提供相应接口供子类完成相关的业务操作
 * 
 * @author 作者 : gavin_fool
 * @date 创建时间：2016年8月22日 上午10:08:06
 * @version 1.0
 */
public class IM_BaseMap implements OnTouchListener, OnClickListener {
	/**
	 * 地图显示控件
	 */
	public MapView m_mapView;
	/**
	 * 地图操作控制
	 */
	private MapControl m_mapControl;
	private Workspace m_workspace;// 地图工作空间
	private Map m_map;// 地图对象
	private WorkspaceConnectionInfo m_wsConnect;// 地图工作空间连接
	private Thread loadMapThread = null;
	private Context mContext;

	private final int LOAD_MAP_OK = 0;// 成功打开地图
	private final int LOAD_MAP_NO_WOEKSPACE = 1;// 无法打开工作空间
	private final int LOAD_MAP_FAULT = 2;// 打开地图失败

	
	public IM_BaseMap(Context mContext) {
		super();
		this.mContext = mContext;
	}

	/**
	 * 初始化地图环境
	 */
	public static void initMapEnvironment(Context context) {
		Environment.setTemporaryPath(Config.ANDROID_TEST_PATH + "/cache/");
		Environment.setWebCacheDirectory(Config.ANDROID_TEST_PATH + "/webCache/");
		Environment.setLicensePath(Config.MAP_LIC_PATH);
		Environment.setOpenGLMode(false);
		Environment.initialization(context);
	}

	public void initMap() {
		/** 加载地图 */
		loadMapThread = new Thread(loadMapRunnable);
		loadMapThread.start();
	}

	public View getMapView(){
//		View v = LayoutInflater.from(mContext).inflate(R.layout.mapview, null);
//		m_mapView = (MapView)v.findViewById(R.id.map_view);
		m_mapView = new MapView(mContext);
		return m_mapView;
	}

	/**
	 * 初始化时，加载地图线程
	 */
	Runnable loadMapRunnable = new Runnable() {

		@Override
		public void run() {
			// 打开地图
			Message msg = mHandler.obtainMessage(R.id.map_view);

			try {
				if (openWorkSpace()) {
					if (m_mapControl == null) {
						m_mapControl = m_mapView.getMapControl();
						m_mapControl.setLongClickable(true);
						m_mapControl.setOnTouchListener(IM_BaseMap.this);
					}
					m_mapControl.getMap().setWorkspace(m_workspace);
					m_mapControl.getMap().open(m_workspace.getMaps().get(0));

					msg.arg1 = LOAD_MAP_OK;// 如果还在当前界面则刷新地图
				} else {
					msg.arg1 = LOAD_MAP_NO_WOEKSPACE;// 如果工作空间不存在所做处理
				}
			} catch (Exception e) {
				msg.arg1 = LOAD_MAP_FAULT;// 加载地图数据异常
			}
			msg.sendToTarget();
		}
	};

	// 打开工作空间消息处理handler
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == R.id.map_view) {
				if (msg.arg1 == LOAD_MAP_OK) {// 如果还在当前界面则刷新地图
					m_mapView.setVisibility(View.VISIBLE);
					if (m_mapControl != null) {
						Log.i("TAG", "BaseMap afterShowCustomData");
						m_mapView.refresh();
						// m_mapControl.getMap().setScale(defaultScale);
						m_mapControl.getMap().refresh();
					}
				} else if (msg.arg1 == LOAD_MAP_NO_WOEKSPACE) {// 工作空间或者地图名称没有
				} else if (msg.arg1 == LOAD_MAP_FAULT) {// 打开工作空间异常
				}
			}
		};
	};

	private boolean openWorkSpace() {
		if (null == m_workspace) {
			m_workspace = new Workspace();
		}
		if (null == m_wsConnect) {
			m_wsConnect = new WorkspaceConnectionInfo();
		}
		if (!"".equals(m_wsConnect.getServer().trim())) {
			// 已经打开工作空间
			return true;
		}
		m_wsConnect.setServer(Config.MAP_ROOT_PATH);
		m_wsConnect.setType(WorkspaceType.SMWU);
		return m_workspace.open(m_wsConnect);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_UP:
			Point point = new Point((int) event.getX(), (int) event.getY());
			Point2D point2d = m_mapControl.getMap().pixelToMap(point);
			GeoPoint geoPoint = new GeoPoint(point2d);
			GeoStyle geoStyle_P = new GeoStyle();
			geoStyle_P.setMarkerAngle(14.0);
			geoStyle_P.setMarkerSize(new Size2D(10, 10));
			geoStyle_P.setMarkerSymbolID(10);
			geoPoint.setStyle(geoStyle_P);
			m_mapControl.addGeometryAddedListener(new GeometryAddedListener() {

				@Override
				public void geometryAdded(GeometryEvent arg0) {
					Log.i("TAG", "add geometry");
				}
			});
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {

	}

}
