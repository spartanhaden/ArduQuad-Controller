package com.hadenw.arduquad;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = ((TextView) findViewById(R.id.label));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public String doConnection() {
		String text;
		// Find all available drivers from attached devices.
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
		if (availableDrivers.isEmpty())
			return "Nothing is connected";
		// Open a connection to the first available driver.
		UsbSerialDriver driver = availableDrivers.get(0);
		if (!(driver instanceof FtdiSerialDriver))
			return "Is not FTDI";
		UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
		if (connection == null)
			return "Permission error";
		UsbSerialPort port = driver.getPorts().get(0);
		try {
			port.open(connection);
			port.setParameters(9600, UsbSerialPort.DATABITS_8,UsbSerialPort.STOPBITS_1,UsbSerialPort.PARITY_NONE);
			byte buffer[] = new byte[5000];
			int numBytesRead = port.read(buffer, 1000);
			text = "Read " + numBytesRead + " bytes. a:" + buffer[0] + " b:" + buffer[1]+ " c:" + buffer[2]+ " d:" + buffer[3]+ " e:" + buffer[4] + " f:" + buffer[5];
			port.close();
			return text;
		} catch (IOException e) {
			return "Errors";
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		textView.setText(doConnection());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
