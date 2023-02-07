package ir.Bprogrammer.ika;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;


public class MainActivity extends Activity { 
	RequestQueue mqueue;
	String UUID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
		UUID = prefs.getString("UUID", "");

		if (UUID.equals("")) {
			Intent move = new Intent(MainActivity.this, RegisterActivity.class);
			move.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(move);
			overridePendingTransition(0, 0);
		} else {
			setContentView(R.layout.activity_main);
		}

		mqueue = Volley.newRequestQueue(this);

		jsonParse(getResources().getString(R.string.ServerURL) + "/api/getJobs?UUID="+UUID);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mainMenuAbout:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.ServerURL) + "/about")));
				return true;

			case R.id.mainMenuHelp:
				AlertDialog dialog = new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("راهنما")
					.setMessage(R.string.help)
					.setPositiveButton("متوجه شدم", null)
					.create();
				dialog.show();
				return true;

			case R.id.mainMenuUpdateJobs:
				jsonParse(getResources().getString(R.string.ServerURL) + "/api/getJobs?UUID="+UUID);
				return true;

			case R.id.mainMenuExit:
				Toast.makeText(this, "برنامه بسته شد!", Toast.LENGTH_SHORT).show();
				finish();
				return(true);
		}

		return super.onOptionsItemSelected(item);
	}
	

	private void jsonParse(String URL) {
		final LinearLayout layout = findViewById(R.id.mainLayout);
		layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);

		final LinearLayout row = new LinearLayout(this);
		row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		row.setOrientation(1);

		JsonObjectRequest request = new JsonObjectRequest(
			Request.Method.GET,
			URL,
			null,
			new Response.Listener<JSONObject>(){
				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONArray jobs = response.getJSONArray("jobs");
						for (int i = 0; i < jobs.length(); i++) {
							JSONObject job = jobs.getJSONObject(i);

							LinearLayout box = new LinearLayout(MainActivity.this);
							TextView titleShower = new TextView(MainActivity.this);
							TextView descriptionShower = new TextView(MainActivity.this);
							LinearLayout ButtonsBox = new LinearLayout(MainActivity.this);
							Button mover = new Button(MainActivity.this);
							Button share = new Button(MainActivity.this);
							LinearLayout divider = new LinearLayout(MainActivity.this);

							final String title = job.getString("title");
							final String description = job.getString("description");
							final String link = job.getString("link");
							final String domain = getResources().getString(R.string.ServerURL);
							
							box.setBackgroundDrawable(getResources().getDrawable(R.drawable.BlurLayoutModel));
							box.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
							box.setOrientation(1);

							titleShower.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
							
							descriptionShower.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
							
							mover.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
							
							share.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
							
							ButtonsBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
							ButtonsBox.setGravity(Gravity.LEFT);
							ButtonsBox.setOrientation(0);

							titleShower.setGravity(Gravity.RIGHT);
							descriptionShower.setGravity(Gravity.RIGHT);
							
							titleShower.setTextColor(Color.WHITE);
							titleShower.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazirblack.ttf"), 1);
							
							descriptionShower.setTextColor(Color.WHITE);
							descriptionShower.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);
							
							mover.setBackground(getResources().getDrawable(R.drawable.ButtonModel));
							mover.setTextColor(Color.WHITE);
							mover.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazirblack.ttf"), 1);
							
							share.setBackground(getResources().getDrawable(R.drawable.NegativeButtonModel));
							share.setTextColor(Color.BLACK);
							share.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/vazir.ttf"), 1);

							titleShower.setText(title);
							descriptionShower.setText(description);
							mover.setText("مشاهده");
							share.setText("اشتراک‌گذاری");

							mover.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
									}
								});
								
							share.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view) {
										Intent sharing = new Intent(Intent.ACTION_SEND);
										sharing.setType("text/plain");
										sharing.putExtra(Intent.EXTRA_TEXT, title+"\n"+description+"\n\n"+"لینک استخدامی : "+link+"\n"+"لینک ثبت‌نام در کاریاب : "+domain);
										startActivity(Intent.createChooser(sharing, "اشتراک‌گذاری با"));
									}
								});
								
							divider.setBackgroundColor(Color.WHITE);
							divider.setPadding(8,8,8,8);
							divider.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


							ButtonsBox.addView(share);
							ButtonsBox.addView(mover);

							box.addView(titleShower);
							box.addView(descriptionShower);
							box.addView(ButtonsBox);
							row.addView(divider);

							row.addView(box);
							
							// notification for alerting user
							NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(MainActivity.this)
								.setSmallIcon(R.drawable.ic_launcher)
								.setContentTitle("کاریار")
								.setContentText("یوهو! شغل های جدیدی رو برات پیدا کردم. بزن روی من تا هدایتت کنم.")
								.setAutoCancel(true);
							Intent intent = new Intent(MainActivity.this, MainActivity.class);
							PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
							mBuilder.setContentIntent(pi);
							NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							mNotificationManager.notify(0, mBuilder.build());

						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError ee) {
					//Toast.makeText(getApplication(), ee+"", Toast.LENGTH_SHORT).show();
					AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setIcon(R.drawable.error)
						.setTitle("مشکلی به وجود آمد")
						.setMessage(ee+"\n"+"به نظر می‌رسد به اینترنت متصل نیستید. لطفا ابتدا از اتصال خود مطمئن شده و سپس مجدداً تلاش کنید.")
						.setPositiveButton("تلاش مجدد", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dia, int which) {
								jsonParse(getResources().getString(R.string.ServerURL) + "/api/getJobs?UUID="+UUID);
							}
						})
						.setNegativeButton("لغو", null)
						.create();
					dialog.show();
				}
			});

		layout.removeAllViews();
		layout.addView(row);
		mqueue.add(request);
	}
} 
