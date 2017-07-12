package com.alier.com.androidtools.ui.imobile.queryService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;
import com.alier.com.androidtools.commons.Config;
import com.alier.com.androidtools.commons.utils.T;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.Geometry;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.WorkspaceVersion;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapInertiaListener;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;
import com.supermap.mapping.RefreshListener;
import com.supermap.services.FeatureSet;
import com.supermap.services.QueryMode;
import com.supermap.services.QueryOption;
import com.supermap.services.QueryService;
import com.supermap.services.ResponseCallback;
import com.supermap.services.ServiceQueryParameter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过地理坐标值查询iserver发布的rest地图服务的要素 1.构建点的缓冲区 2.利用缓冲区进行空间的相交查询出道路 3.计算出离点最近的道路
 * 并返回该要素的信息
 */
public class QueryServiceActivity extends BaseActivity
		implements MapParameterChangedListener, RefreshListener, OnClickListener {

	private Workspace myWorkspace;
	private MapView myMapView;
	private MapControl myMapControl;
	private Dataset myDataset;
	private QueryService service = new QueryService("http://172.16.211.206:8090/iserver");
	private String mapPath = Config.ROOT_PATH + "/SuperMap/cwmap/restmap/CWMap.smwu";
	private Button btnQuery;
	private ZoomControls zoomControl;
	private Map<Integer, String> hashMap;// 存储要素的ID和name值
	private TextView scaleText, realWidth, partNum;
	private int m_width, m_height;
	private Rectangle2D rectangle2D = null;
	private Point2D point2DLeftButtomSc;// 地图左下角坐标
	private Point2D point2DRightTopSc;// 地图右上角坐标
	private Point2D lastCenterPoint;// 上次请求部件信息时的地图中心点
	private Toast mToast;
	private boolean isShowPart = false;// 时候显示部件，当地图比例尺在小于0.0001是不显示
	private boolean isFirstRequestPart = true;// 是否是第一次去请求部件
	private double realWidthInScale = 0;// 当前比例尺下屏幕宽度实际表示的距离
	private double maxWidthToRequest = 140;
	private Point2D localCenterPoint2D;// 地图中心点，用于判断地图是否移动
	
	NumberFormat nFormat = NumberFormat.getInstance();
	Thread requestPartThread = null;
	private double localScale;// 当前地图比例尺
	private boolean isStopMap = false;
	// 程序入口，绘制界面

	@Override
	public void init() {
		// 初始化环境
		Environment.setLicensePath(Config.MAP_LIC_PATH);
		Environment.setWebCacheDirectory(Config.ROOT_PATH + "/SuperMap/cache/");
		Environment.setWebCacheDirectory(Config.ROOT_PATH + "/SuperMap/webcache/");
		Environment.initialization(this);
		setContentView(R.layout.queryservice);
		initViews();
		nFormat.setMaximumFractionDigits(7);
		openLocalMap();
		// openOnlineMap();
	}

	@Override
	public void exec() {
		requestPartThread = new Thread(RequestPartRunnable);
		requestPartThread.start();
	}

	/**
	 * 打开iserver发布的rest地图服务
	 * 
	 * @return
	 */
	private boolean openOnlineMap() {
		myWorkspace = new Workspace();
		myMapView = (MapView) findViewById(R.id.mapView);
		myMapControl = myMapView.getMapControl();
		myMapControl.getMap().setWorkspace(myWorkspace);
		myMapControl.setMapParamChangedListener(this);
		DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
		connectionInfo.setServer("http://172.16.211.206:8090/iserver/services/map-CWMap/rest/maps/CWMap");
		connectionInfo.setEngineType(EngineType.Rest);
		connectionInfo.setAlias("崇文地图");
		Datasource datasource = myWorkspace.getDatasources().open(connectionInfo);
		if (datasource != null) {
			myDataset = datasource.getDatasets().get(0);
			myMapControl.getMap().getLayers().add(myDataset, true);
			myMapControl.getMap().refresh();
			return true;
		}
		Log.e(this.getClass().getName(), "打开数据源失败了");
		myWorkspace.dispose();
		return false;
	}

	private void openLocalMap() {
		myWorkspace = new Workspace();
		myMapView = (MapView) this.findViewById(R.id.mapView);
		myMapControl = myMapView.getMapControl();
		myMapControl.setMapParamChangedListener(this);
		myMapControl.setRefreshListener(this);
		WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo();
		connectionInfo.setServer(mapPath);
		connectionInfo.setType(WorkspaceType.SMWU);
		connectionInfo.setVersion(WorkspaceVersion.UGC70);
		myWorkspace.open(connectionInfo);
		myMapControl.getMap().setWorkspace(myWorkspace);
		myMapControl.getMap().open(myWorkspace.getMaps().get(0));
		myMapControl.getMap().setScale(0.0005);
		myMapControl.getMap().refresh();
	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		scaleText = (TextView) this.findViewById(R.id.scale_txt);
		realWidth = (TextView) this.findViewById(R.id.realwidth);
		partNum = (TextView) this.findViewById(R.id.partNum);
		btnQuery = (Button) this.findViewById(R.id.btn_query);
		zoomControl = (ZoomControls) this.findViewById(R.id.zoomControl);
		zoomControl.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myMapControl.getMap().zoom(2);
				myMapControl.getMap().refresh();
			}
		});
		zoomControl.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myMapControl.getMap().zoom(0.5);
				myMapControl.getMap().refresh();
			}
		});
		btnQuery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		this.m_width = getWindowManager().getDefaultDisplay().getWidth();
		this.m_height = getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 * 获取服务查询参数
	 *
	 * @return
	 */
	private ServiceQueryParameter getQueryParameter() {
		ServiceQueryParameter parameter = new ServiceQueryParameter();
		parameter.setQueryServiceName("map-CWMap/rest");// 服务实例名称
		parameter.setQueryMapName("CWMap");// 地图名称
		parameter.setQueryLayerName("P0101@GRID_SYSDB");
		parameter.setQueryOption(QueryOption.ATTRIBUTEANDGEOMETRY);
		parameter.setQueryRecordStart(0);
		parameter.setExpectRecordCount(1000000);
		parameter.setQueryBounds(rectangle2D);
		return parameter;
	}

	/**
	 * 展示结果街道地址
	 * 
	 * @param geometry
	 */
	private void showMaker(Geometry geometry) {
		Point2D point2D = geometry.getInnerPoint();
		ImageView view = new ImageView(QueryServiceActivity.this);
		view.setBackgroundResource(R.drawable.local_pisition);
		CallOut callOut = new CallOut(QueryServiceActivity.this);
		callOut.setTag(point2D);
		callOut.setEnabled(true);
		callOut.setOnClickListener(this);
		callOut.setContentView(view);
		callOut.setCustomize(true);
		callOut.setLocation(point2D.getX(), point2D.getY());
		myMapView.addCallout(callOut);
		myMapView.showCallOut();
	}

	/**
	 * @Description:范围查询
	 */
	private void BoundsQuery() {
		service.setResponseCallback(new ResponseCallback() {
			public void requestFailed(String s) {
//				T.showLong(QueryServiceActivity.this, "没有查询到结果");
				Message msg = new Message();
				msg.arg1 = 1;// 失败
				BoundsQueryHandler.sendMessage(msg);
			}

			public void requestSuccess() {
			}

			public void receiveResponse(FeatureSet arg) {
				if (null == arg) {
					// T.showLong(QueryServiceActivity.this, "没有查询到结果");
					Message msg = new Message();
					msg.arg1 = 2;
					BoundsQueryHandler.sendMessage(msg);
					return;
				}
//				T.showLong(QueryServiceActivity.this, "没有查询到结果");
				int count = arg.getFeatureCount();
				if (count > 0) {
					if (arg instanceof FeatureSet) {
						Log.i("TAG", "查询结束时间:" + new Date().getTime());
						arg.moveFirst();
						ArrayList<Geometry> list = new ArrayList<Geometry>();
						hashMap = new HashMap<Integer, String>();
						while (!arg.isEOF()) {
							Geometry geometry = arg.getGeometry();
							FieldInfos fieldInfos = arg.getFieldInfos();
							int co = fieldInfos.getCount();
							FieldInfo fieldInfo = fieldInfos.get(0);
							String s = fieldInfo.getName();
							String a = fieldInfo.getCaption();
							fieldInfo.getDefaultValue();
							fieldInfo.getHandle();
							arg.getFieldValue("SMID").toString();
							arg.getFieldValue("OBJCODE").toString();
							if (geometry == null) {
								arg.moveNext();
								continue;
							}
							list.add(geometry);
							int id = arg.getGeometry().getInnerPoint().hashCode();
							hashMap.put(id, arg.getFieldValue("ObjName").toString());
							// showMaker(geometry);
							arg.moveNext();

						}
						Message msg = new Message();
						msg.arg1 = 0;
						msg.obj = list;
						BoundsQueryHandler.sendMessage(msg);
					}
				} else {
					Toast.makeText(QueryServiceActivity.this, "该坐标点不在道路附近，请重新设置坐标点", Toast.LENGTH_SHORT).show();
				}
			}

			public void dataServiceFinished(String s) {
			}
		});
		service.query(getQueryParameter(), QueryMode.BoundsQuery);// 空间查询
		Log.i("TAG", "开始查询时间：" + new Date().getTime());
	}

	/**
	 * 处理范围查询handler
	 */
	Handler BoundsQueryHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case 0:// 成功
				myMapView.removeAllCallOut();
				ArrayList<Geometry> list = (ArrayList<Geometry>) msg.obj;
				partNum.setText("部件数量：" + list.size());
				for (Geometry geometry : list) {
					showMaker(geometry);
				}
				isShowPart = true;
				myMapControl.getMap().refresh();
				Log.i("TAG", "叠加成功时间:" + new Date().getTime());
				break;
			case 1:// 失败
				Toast.makeText(QueryServiceActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
				break;
			case 2:// 没有数据
					// Toast.makeText(QueryServiceActivity.this, "没有数据",
					// Toast.LENGTH_SHORT).show();
				break;
			case 3:// 清除图标
				myMapView.removeAllCallOut();
				isShowPart = false;
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void angleChanged(double arg0) {
		// T.showShort(QueryServiceActivity.this, arg0+"");
	}

	long lastTime = 0;

	@Override
	public void boundsChanged(Point2D bounds) {// 范围
		Log.i("TAG", "地图中心点：" + bounds.toString() + ":" + new Date().getTime());
		localCenterPoint2D = bounds;
		// isRequestPart(localCenterPoint2D, localScale);
	}

	@Override
	public void scaleChanged(double arg0) {// 比例尺
		localScale = arg0;
		Point2D point1 = myMapControl.getMap().pixelToMap(new Point(0, 0));
		Point2D point2 = myMapControl.getMap().pixelToMap(new Point(m_width, 0));
		realWidthInScale = point2.getX() - point1.getX();
		realWidth.setText(String.valueOf(realWidthInScale));
		scaleText.setText(String.valueOf(nFormat.format(arg0)));
		// isRequestPart(localCenterPoint2D, localScale);
	}

	private void isRequestPart(Point2D localCenterPoint2D, double localScale) {
		if (localScale < 0.0005) {
			if (isShowPart) {
				Message msg = new Message();
				msg.arg1 = 3;
				BoundsQueryHandler.sendMessage(msg);
			}
			return;
		}
		point2DLeftButtomSc = myMapControl.getMap().pixelToMap(new Point(0, m_height));
		point2DRightTopSc = myMapControl.getMap().pixelToMap(new Point(m_width, 0));

		rectangle2D = new Rectangle2D(new Point2D(point2DLeftButtomSc.getX() - 140, point2DLeftButtomSc.getY() - 140),
				new Point2D(point2DRightTopSc.getX() + 140, point2DRightTopSc.getY() + 140));
		if (isFirstRequestPart) {
			BoundsQuery();
			isFirstRequestPart = false;
			lastCenterPoint = myMapControl.getMap().pixelToMap(new Point(m_width / 2, m_height / 2));
			Point2D point1 = myMapControl.getMap().pixelToMap(new Point(0, 0));
			Point2D point2 = myMapControl.getMap().pixelToMap(new Point(m_width, 0));
			// maxWidthToRequest = point2.getX() - point1.getX();
			return;
		}
		if (localCenterPoint2D == null) {
			return;
		}
		if (!isShowPart || Math.abs(lastCenterPoint.getX() - localCenterPoint2D.getX()) > maxWidthToRequest
				|| Math.abs(lastCenterPoint.getY() - localCenterPoint2D.getY()) > maxWidthToRequest) {
			BoundsQuery();
			lastCenterPoint = localCenterPoint2D;
		}

	}

	@Override
	public void sizeChanged(int arg0, int arg1) {// 地图尺寸

	}

	int i = 1;

	private boolean isRefreshed = false;//

	@Override
	public void mapRefresh() {
		Log.i("TAG", "地图刷新" + new Date().getTime() + "");
		i++;
		isStopMap = false;
		isRefreshed = true;
	}

	/**
	 * 请求部件线程
	 */
	Runnable RequestPartRunnable = new Runnable() {
		public void run() {
			while (true) {
				if (isStopMap && isRefreshed) {
					isRefreshed = false;
					isRequestPart(localCenterPoint2D, localScale);
				}
				try {
					isStopMap = true;
					Thread.sleep(200);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		Point2D point = (Point2D) v.getTag();
		T.show(QueryServiceActivity.this, "点击callout" + point.toString(), Toast.LENGTH_LONG);
	}
}
