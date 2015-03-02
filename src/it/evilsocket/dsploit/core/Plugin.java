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
package it.evilsocket.dsploit.core;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import it.evilsocket.dsploit.*;
import it.evilsocket.dsploit.net.*;
import it.evilsocket.dsploit.net.Target.*;

public abstract class Plugin extends Activity 
{
	public static final int NO_LAYOUT = -1;
	
	private String        mName				  = null;
	private String		  mDescription		  = null;
	private Target.Type[] mAllowedTargetTypes = null;
	private int			  mLayoutId			  = 0;
	private int			  mIconId		      = 0;
		
	public Plugin( String name, String description, Target.Type[] allowedTargetTypes, int layoutId, int iconResourceId ){
		mName 				= name;
		mDescription	    = description;
		mAllowedTargetTypes = allowedTargetTypes;
		mLayoutId			= layoutId;
		mIconId				= iconResourceId;
	}
	
	public Plugin( String name, String description, Target.Type[] allowedTargetTypes, int layoutId ){
		this( name, description, allowedTargetTypes, layoutId, R.drawable.action_plugin );
	}
	
	public String getName(){
		return mName;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	public Target.Type[] getAllowedTargetTypes(){
		return mAllowedTargetTypes;
	}
	
	public int getIconResourceId(){
		return mIconId;
	}
	
	public boolean isAllowedTarget( Target target ){
		for( Target.Type type : mAllowedTargetTypes )
			if( type == target.getType() )
				return true;
		
		return false;
	}	
	
	public boolean hasLayoutToShow() {
		return mLayoutId != -1;
	}
	
	public void onActionClick( Context context ){
		
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             
        setTitle( System.getCurrentTarget() + " > " + mName );
        setContentView( mLayoutId );     
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) 
	{    
		switch( item.getItemId() ) 
		{        
			case android.R.id.home:            
	         
				onBackPressed();
				
				return true;
	    	  
			default:            
				return super.onOptionsItemSelected(item);    
	   }
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void onTargetNewOpenPort( Target target, Port port ) {
		
	}
	
	public void onTargetNewVulnerability( Target target, Port port, Vulnerability vulnerability ) {
		
	}
}
