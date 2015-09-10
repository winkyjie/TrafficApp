package cn.fszt.trafficapp.util.downZip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.ui.activity.WelcomeActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
	 String _webDir;
	 private final String TAG = "DownLoaderTask";
	 private URL mUrl;
	 private File mFile;
	 private ProgressReportingOutputStream mOutputStream;
	 private Context mContext;
	 private String url,out,filename;
	 private SharedPreferences sp;
	 private Editor editor;
	 public DownLoaderTask(String url,String out,Context context){
	  super();
	  if(context!=null){
	   mContext = context;
	   this.url = url;
	   this.out = out;
	   sp = context.getSharedPreferences("WEB", Context.MODE_PRIVATE);
	   editor = sp.edit();
	   _webDir = mContext.getFilesDir().getPath()+mContext.getString(R.string.web_dir);
	  }
	 }

	 @Override
	 protected void onPreExecute() {
	 }
	 @Override
	 protected Long doInBackground(Void... params) {
	  return download();
	 }
	 
	 @Override
	 protected void onProgressUpdate(Integer... values) {
	 }
	 @Override
	 protected void onPostExecute(Long result) {
	  if(isCancelled())
	   return;
	  //没有更新网页，不需要解压
	  if(filename!=null){
		  ((WelcomeActivity)mContext).doZipExtractorWork(); 
//		  System.out.println("更新解压");
	  }else{
//		  System.out.println("不用更新");
	  }
	 }
	 
	 
	 private long download(){
	  URLConnection connection = null;
	  
	  int bytesCopied = 0;
	  try {
	   mUrl = new URL(url);
	   connection = mUrl.openConnection();
	   int length = connection.getContentLength();
	   filename = connection.getHeaderField("Content-Disposition");
	   if(filename == null){
	   }else{
		   //删除原有文件夹
		   File old_webDir = new File(_webDir);
		   if (old_webDir.exists()) {
			   old_webDir.delete();
		   }
		   
		   //新建文件夹
		   File new_webDir = new File(_webDir);
		   if (!new_webDir.exists()) {
			   new_webDir.mkdir();
		   }
		   
		   filename = filename.substring(filename.indexOf("filename=\"")+10,filename.length()-1);
		   String[] arr_filename = filename.split("_");
		   editor.putString("timestamp", arr_filename[0]);
		   editor.commit();
		   mFile = new File(out, "web.zip");
		   if(mFile.exists()&&length == mFile.length()){
		    Log.d(TAG, "file "+mFile.getName()+" already exits!!");
		    return 0l;
		   }
		   mOutputStream = new ProgressReportingOutputStream(mFile);
		   publishProgress(0,length);
		   bytesCopied =copy(connection.getInputStream(),mOutputStream);
		   mOutputStream.close();
	   }
	  } catch (IOException e) {
	   e.printStackTrace();
	  }
	  return bytesCopied;
	 }
	 
	 private int copy(InputStream input, OutputStream output){
	  byte[] buffer = new byte[1024*8];
	  BufferedInputStream in = new BufferedInputStream(input, 1024*8);
	  BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
	  int count =0,n=0;
	  try {
	   while((n=in.read(buffer, 0, 1024*8))!=-1){
	    out.write(buffer, 0, n);
	    count+=n;
	   }
	   out.flush();
	  } catch (IOException e) {
	   e.printStackTrace();
	  }finally{
	   try {
	    out.close();
	   } catch (IOException e) {
	    e.printStackTrace();
	   }
	   try {
	    in.close();
	   } catch (IOException e) {
	    e.printStackTrace();
	   }
	  }
	  return count;
	 }
	 private final class ProgressReportingOutputStream extends FileOutputStream{
	  public ProgressReportingOutputStream(File file)
	    throws FileNotFoundException {
	   super(file);
	  }
	  @Override
	  public void write(byte[] buffer, int byteOffset, int byteCount)
	    throws IOException {
	   super.write(buffer, byteOffset, byteCount);
	  }

	 }
	}
