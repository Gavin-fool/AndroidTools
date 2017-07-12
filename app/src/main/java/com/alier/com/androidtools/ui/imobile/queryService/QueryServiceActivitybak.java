package com.alier.com.androidtools.ui.imobile.queryService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.alier.com.androidtools.R;
import com.alier.com.androidtools.commons.BaseActivity;
import com.alier.com.androidtools.commons.Config;
import com.alier.com.androidtools.commons.utils.T;
import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.analyst.BufferRadiusUnit;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.Geometrist;
import com.supermap.data.Geometry;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.data.WorkspaceVersion;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapParameterChangedListener;
import com.supermap.mapping.MapView;
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
import java.util.Map;

/**
 * 通过地理坐标值查询iserver发布的rest地图服务的要素 1.构建点的缓冲区 2.利用缓冲区进行空间的相交查询出道路 3.计算出离点最近的道路
 * 并返回该要素的信息
 */
public class QueryServiceActivitybak extends BaseActivity implements MapParameterChangedListener {

	private Workspace myWorkspace;
	private MapView myMapView;
	private MapControl myMapControl;
	private Dataset myDataset;
	private GeoRegion myGeoRegion;// 缓冲区圆对象
	private EditText etX;// x坐标值
	private EditText etY;// y坐标值
	private QueryService service = new QueryService("http://172.16.211.206:8090/iserver");
	private String mapPath = Config.ROOT_PATH + "/SuperMap/cwmap/restmap/CWMap.smwu";
	private Button btnQuery;
	private ZoomControls zoomControl;
	private Map<Integer, String> hashMap;// 存储要素的ID和name值
	private TextView scaleText, realWidth,partNum;
	private int m_width, m_height;
	private Rectangle2D rectangle2D = null;
	private Point2D point2DLeftButtomSc;// 地图左下角坐标
	private Point2D point2DRightTopSc;// 地图右上角坐标
	private Point2D lastCenterPoint;// 上次请求部件信息时的地图中心点

	private boolean isShowPart = false;// 时候显示部件，当地图比例尺在小于0.0001是不显示
	private boolean isFirstRequestPart = true;// 是否是第一次去请求部件
	private double realWidthInScale = 0;// 当前比例尺下屏幕宽度实际表示的距离
	private double maxWidthToRequest = 0;
	private Point2D localCenterPoint2D;// 地图中心点，用于判断地图是否移动
	private double localScale;// 当前地图比例尺
	NumberFormat nFormat = NumberFormat.getInstance();
	
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
		etX = (EditText) findViewById(R.id.et_x);
		etY = (EditText) findViewById(R.id.et_y);
		scaleText = (TextView) this.findViewById(R.id.scale_txt);
		realWidth = (TextView) this.findViewById(R.id.realwidth);
		partNum = (TextView)this.findViewById(R.id.partNum);
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
				queryBySpatial();
			}
		});
		this.m_width = getWindowManager().getDefaultDisplay().getWidth();
		this.m_height = getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 * 根据点构建半径为10米的缓冲区
	 *
	 * @param geoPoint
	 * @return 返回一个圆
	 */
	private GeoRegion makeBuffer(GeoPoint geoPoint) {
		BufferAnalystParameter parameter = new BufferAnalystParameter();
		parameter.setEndType(BufferEndType.ROUND);
		parameter.setLeftDistance(500);
		parameter.setRightDistance(500);
		parameter.setRadiusUnit(BufferRadiusUnit.Meter);
		return BufferAnalystGeometry.createBuffer(geoPoint, parameter, myDataset.getPrjCoordSys());
	}

	/**
	 * 判断传入的坐标值是否在范围内
	 *
	 * @param cur
	 * @param min
	 * @param max
	 * @return
	 */
	private boolean isInBounds(double cur, double min, double max) {
		return Math.min(cur, max) == Math.max(cur, min);
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
		parameter.setExpectRecordCount(1000);
		parameter.setQueryBounds(rectangle2D);
		// parameter.setExpectRecordCount();
		// parameter.setAttributeFilter("SMID=3");//SQL查询
		// parameter.setSpatialQueryMode(SpatialQueryMode.INTERSECT);// 面与线的相交
		// parameter.setQueryGeomety(rectangle2D);
		return parameter;
	}

	/**
	 * 计算距离点最近的道路
	 * 
	 * @param point
	 * @param geometries
	 * @return 返回道路对象及距离
	 */
	private Geometry computeDistance(GeoPoint point, ArrayList<Geometry> geometries) {
		int size = geometries.size();
		double lenth = 500.0;
		double temp = 0.0;
		double minLenth;// 最短距离
		int flag = 0;
		for (int i = 0; i < size; i++) {
			temp = Geometrist.distance(point, geometries.get(i));
			lenth = Math.min(temp, lenth);
			if (temp == lenth) {
				flag = i;
				minLenth = lenth;
			}
		}
		return geometries.get(flag);
	}

	/**
	 * 展示结果街道地址
	 * 
	 * @param geometry
	 */
	private void showMaker(Geometry geometry) {
		Point2D point2D = geometry.getInnerPoint();
		// String name = arg.getString("name");
		// LayoutInflater inflater =
		// QueryServiceActivity.this.getLayoutInflater();
		// View callOutView = inflater.inflate(R.layout.callout, null);
		ImageView view = new ImageView(QueryServiceActivitybak.this);
		view.setBackgroundResource(R.drawable.local_pisition);
		CallOut callOut = new CallOut(QueryServiceActivitybak.this);
		callOut.setContentView(view);
		callOut.setCustomize(true);
		callOut.setLocation(point2D.getX(), point2D.getY());
		myMapView.addCallout(callOut);
		// TextView textView = (TextView) findViewById(R.id.tv_name);
		// int id = point2D.hashCode();
		// String name = hashMap.get(id);
		// textView.setText(name);
		myMapView.showCallOut();
		// myMapControl.getMap().setCenter(point2D);
	}

	/**
	 * 空间查询iserver服务
	 */
	private void queryBySpatial() {
		// String strX = etX.getText().toString();
		// String strY = etY.getText().toString();
		// final double x = Double.valueOf(strX);
		// final double y = Double.valueOf(strY);
		/*
		 * double minX = myDataset.getBounds().getLeft(); double maxX =
		 * myDataset.getBounds().getRight(); double minY =
		 * myDataset.getBounds().getBottom(); double maxY =
		 * myDataset.getBounds().getTop(); if (isInBounds(x, minX, maxX) &&
		 * isInBounds(y, minY, maxY)) { myGeoRegion = makeBuffer(new GeoPoint(x,
		 * y)); } else { String str = "请输入范围内坐标值：X(" + String.valueOf(minX) +
		 * "-" + String.valueOf(maxX) + "),Y(" + String.valueOf(minY) + "-" +
		 * String.valueOf(maxY) + ")"; Toast.makeText(QueryServiceActivity.this,
		 * str, Toast.LENGTH_SHORT).show(); return; }
		 */
		myMapView.removeAllCallOut();
		QueryService service = new QueryService("http://172.16.211.206:8090/iserver");
		service.setResponseCallback(new ResponseCallback() {
			public void requestFailed(String s) {
				Toast.makeText(QueryServiceActivitybak.this, "查询失败", Toast.LENGTH_SHORT).show();
			}

			public void requestSuccess() {
				Toast.makeText(QueryServiceActivitybak.this, "查询成功", Toast.LENGTH_SHORT).show();
			}

			public void receiveResponse(FeatureSet arg) {
				if (null == arg) {
					T.showLong(QueryServiceActivitybak.this, "没有查询到结果");
					return;
				}
				int count = arg.getFeatureCount();
				if (count > 0) {
					if (arg instanceof FeatureSet) {
						arg.moveFirst();
						ArrayList<Geometry> list = new ArrayList<Geometry>();
						hashMap = new HashMap<Integer, String>();
						while (!arg.isEOF()) {
							Geometry geometry = arg.getGeometry();
							if (geometry == null) {
								arg.moveNext();
								continue;
							}
							list.add(geometry);
							int id = arg.getGeometry().getInnerPoint().hashCode();
							hashMap.put(id, arg.getFieldValue("ObjName").toString());
							showMaker(geometry);
							arg.moveNext();
						}
						// showMaker(computeDistance(new GeoPoint(x, y), list));
					}
				} else {
					Toast.makeText(QueryServiceActivitybak.this, "该坐标点不在道路附近，请重新设置坐标点", Toast.LENGTH_SHORT).show();
				}
			}

			public void dataServiceFinished(String s) {
			}
		});
		service.query(getQueryParameter(), QueryMode.BoundsQuery);// 空间查询
	}

	/**
	 * @Description:范围查询
	 */
	private void BoundsQuery() {
		service.setResponseCallback(new ResponseCallback() {
			public void requestFailed(String s) {
				Message msg = new Message();
				msg.arg1 = 1;// 失败
				BoundsQueryHandler.sendMessage(msg);
			}

			public void requestSuccess() {
				// Toast.makeText(QueryServiceActivity.this, "查询成功",
				// Toast.LENGTH_SHORT).show();
			}

			public void receiveResponse(FeatureSet arg) {
				if (null == arg) {
					// T.showLong(QueryServiceActivity.this, "没有查询到结果");
					Message msg = new Message();
					msg.arg1 = 2;
					BoundsQueryHandler.sendMessage(msg);
					return;
				}
				int count = arg.getFeatureCount();
				if (count > 0) {
					if (arg instanceof FeatureSet) {
						Log.i("TAG", "查询结束时间:" + new Date().getTime());
						arg.moveFirst();
						ArrayList<Geometry> list = new ArrayList<Geometry>();
						hashMap = new HashMap<Integer, String>();
						while (!arg.isEOF()) {
							Geometry geometry = arg.getGeometry();
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
					Toast.makeText(QueryServiceActivitybak.this, "该坐标点不在道路附近，请重新设置坐标点", Toast.LENGTH_SHORT).show();
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
				partNum.setText("部件数量："+list.size());
				for (Geometry geometry : list) {
					showMaker(geometry);
				}
				isShowPart = true;
				myMapControl.getMap().refresh();
				Log.i("TAG", "叠加成功时间:" + new Date().getTime());
				break;
			case 1:// 失败
				Toast.makeText(QueryServiceActivitybak.this, "查询失败", Toast.LENGTH_SHORT).show();
				break;
			case 2:// 没有数据
				Toast.makeText(QueryServiceActivitybak.this, "没有数据", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	private void RequestPart() {
		point2DLeftButtomSc = myMapControl.getMap().pixelToMap(new Point(-m_width, 2 * m_height));
		point2DRightTopSc = myMapControl.getMap().pixelToMap(new Point(2 * m_width, -m_height));
		rectangle2D = new Rectangle2D(point2DLeftButtomSc, point2DRightTopSc);
		new Thread(new Runnable() {
			public void run() {
				BoundsQuery();
			}
		}).start();
	}

	@Override
	public void angleChanged(double arg0) {
		// T.showShort(QueryServiceActivity.this, arg0+"");
	}

	long lastTime = 0;

	@Override
	public void boundsChanged(Point2D bounds) {// 范围
		localCenterPoint2D = bounds;
		isRequestPart(localCenterPoint2D, localScale);
	}

	@Override
	public void scaleChanged(double arg0) {// 比例尺
		localScale = arg0;
		// T.showShort(QueryServiceActivity.this, arg0+"");
		Point2D point1 = myMapControl.getMap().pixelToMap(new Point(0, 0));
		Point2D point2 = myMapControl.getMap().pixelToMap(new Point(m_width, 0));
		realWidthInScale = point2.getX() - point1.getX();
		realWidth.setText(String.valueOf(realWidthInScale));
		scaleText.setText(String.valueOf(nFormat.format(arg0)));
		isRequestPart(localCenterPoint2D, localScale);
	}

	private void isRequestPart(Point2D localCenterPoint2D, double localScale) {
		if (localScale < 0.0005) {
			if (isShowPart) {
				myMapView.removeAllCallOut();
				isShowPart = false;
			}
			return;
		}
		if (isFirstRequestPart) {
			RequestPart();
			isFirstRequestPart = false;
			lastCenterPoint = myMapControl.getMap().pixelToMap(new Point(m_width / 2, m_height / 2));
			Point2D point1 = myMapControl.getMap().pixelToMap(new Point(0, 0));
			Point2D point2 = myMapControl.getMap().pixelToMap(new Point(m_width, 0));
			maxWidthToRequest = point2.getX() - point1.getX();
			return;
		}
		if (!isShowPart || Math.abs(lastCenterPoint.getX() - localCenterPoint2D.getX()) > maxWidthToRequest
				|| Math.abs(lastCenterPoint.getY() - localCenterPoint2D.getY()) > maxWidthToRequest) {
			long tm = new Date().getTime();
			if (tm - lastTime > 200) {

			}
			RequestPart();
			lastCenterPoint = localCenterPoint2D;
		}

	}

	@Override
	public void sizeChanged(int arg0, int arg1) {// 地图尺寸
		// T.showShort(QueryServiceActivity.this, arg0 + ":" + arg1);

	}

}
