//package upenn.pennapps2012f;
//
//import com.google.api.services.calendar.model.CalendarList;
//import com.google.api.services.calendar.model.CalendarListEntry;
//
//import android.app.ProgressDialog;
//import android.os.AsyncTask;
//
//import java.io.IOException;
//
///**
// * Asynchronously load the calendars with a progress dialog.
// *
// * @author Ravi Mistry
// */
//class AsyncLoadCalendars extends AsyncTask<Void, Void, Void> {
//
//  private final CalendarSample calendarSample;
//  private final ProgressDialog dialog;
//  private com.google.api.services.calendar.Calendar client;
//
//  AsyncLoadCalendars(CalendarSample calendarSample) {
//    this.calendarSample = calendarSample;
//    client = calendarSample.client;
//    dialog = new ProgressDialog(calendarSample);
//  }
//
//  @Override
//  protected void onPreExecute() {
//    dialog.setMessage("Loading calendars...");
//    dialog.show();
//  }
//
//  @Override
//  protected Void doInBackground(Void... arg0) {
//    try {
//      calendarSample.calendars.clear();
//      com.google.api.services.calendar.Calendar.CalendarList.List list =
//          client.calendarList().list();
//      list.setFields("items");
//      CalendarList feed = list.execute();
//      if (feed.getItems() != null) {
//        for (CalendarListEntry calendar : feed.getItems()) {
//          CalendarInfo info = new CalendarInfo(calendar.getId(), calendar.getSummary());
//          calendarSample.calendars.add(info);
//        }
//      }
//    } catch (IOException e) {
//      calendarSample.handleGoogleException(e);
//    } finally {
//      calendarSample.onRequestCompleted();
//    }
//    return null;
//  }
//
//  @Override
//  protected void onPostExecute(Void result) {
//    dialog.dismiss();
//    calendarSample.refresh();
//  }
//}