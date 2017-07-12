package com.alier.com.androidtools.ui.robotium_Test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.alier.com.androidtools.R;

public class RobotiumTestActivity extends Activity implements OnClickListener{

	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robotium_test);
		initView();
	}
	
	private void initView(){
		editText1 = (EditText)this.findViewById(R.id.editText1);
		editText2 = (EditText)this.findViewById(R.id.editText2);
		editText3 = (EditText)this.findViewById(R.id.editText3);
		button = (Button)this.findViewById(R.id.button1);
		button.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button1){
			int result = Integer.parseInt(editText1.getText().toString().trim())+Integer.parseInt(editText2.getText().toString().trim());
			editText3.setText(String.valueOf(result));
		}
	}

}
