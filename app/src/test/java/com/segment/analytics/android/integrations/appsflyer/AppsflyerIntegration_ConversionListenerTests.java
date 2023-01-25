package com.segment.analytics.android.integrations.appsflyer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.ValueMap;
import static  org.mockito.Mockito.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AppsflyerIntegration_ConversionListenerTests {

    @Test
    public void testAppsflyerIntegration_ConversionListener_ctor_happyFlow() throws Exception {
        Analytics analytics = mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));

        Assert.assertTrue(conversionListener.analytics==analytics);

        reset(analytics,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_ctor_nullFlow() throws Exception {
        Analytics analytics = null;
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));

        Assert.assertTrue(conversionListener.analytics==analytics);

        reset(conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onConversionDataSuccess_happyFlow() throws Exception {
        //I want just to check the conversionListener gets the map.
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = mock(Analytics.class);
        Map<String, Object> conversionData =  new ValueMap();
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean("AF_onConversion_Data",false)).thenReturn(true);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));

        conversionListener.onConversionDataSuccess(conversionData);

        verify(AppsflyerIntegration.conversionListener).onConversionDataSuccess(conversionData);

        reset(AppsflyerIntegration.conversionListener,analytics,app,context,sharedPreferences,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onAttributionFailure_happyFlow() throws Exception {
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = Mockito.mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        String errorMsg = "error - test";

        conversionListener.onAttributionFailure(errorMsg);

        verify(AppsflyerIntegration.conversionListener,times(1)).onAttributionFailure(errorMsg);

        reset(analytics,conversionListener,AppsflyerIntegration.conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_onAttributionFailure_nullFlow() throws Exception {
        AppsflyerIntegration.conversionListener = mock(AppsflyerIntegration.ExternalAppsFlyerConversionListener.class);
        Analytics analytics = Mockito.mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        String errorMsg = null;
        conversionListener.onAttributionFailure(errorMsg);
        verify(AppsflyerIntegration.conversionListener,times(1)).onAttributionFailure(null);

        reset(analytics,conversionListener,AppsflyerIntegration.conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_trackInstallAttributed_happyFlow() throws Exception {
        Analytics analytics =mock(Analytics.class);
        Map<String, Object> attributionData = new HashMap<String, Object>()
        {
            {
                put("media_source", "media_source_moris");
                put("campaign", "campaign_moris");
                put("adgroup", "adgroup_moris");
            }
        };
        Map<String, Object> campaign = new ValueMap() //
                .putValue("source", attributionData.get("media_source"))
                .putValue("name", attributionData.get("campaign"))
                .putValue("ad_group", attributionData.get("adgroup"));
        Properties properties = new Properties().putValue("provider", "AppsFlyer");
        properties.putAll(attributionData);
        properties.remove("media_source");
        properties.remove("adgroup");
        properties.putValue("campaign", campaign);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));

        conversionListener.trackInstallAttributed(attributionData);

        verify(analytics,times(1)).track("Install Attributed", properties);

        reset(analytics,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_trackInstallAttributed_negativeFlow() throws Exception {
        Analytics analytics =mock(Analytics.class);
        Map<String, Object> attributionData = new HashMap<String, Object>();
        Map<String, Object> campaign = new ValueMap() //
                .putValue("source", "")
                .putValue("name", "")
                .putValue("ad_group", "");
        Properties properties = new Properties().putValue("provider", "AppsFlyer");
        properties.putAll(attributionData);
        properties.remove("media_source");
        properties.remove("adgroup");
        properties.putValue("campaign", campaign);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        conversionListener.trackInstallAttributed(attributionData);

        verify(analytics,times(1)).track("Install Attributed", properties);

        reset(analytics,conversionListener);
    }

//This flow breaks because there is no check for attributionData!=null in trackInstallAttributed method.
//    @Test
//    public void testAppsflyerIntegration_trackInstallAttributed_nullFlow() throws Exception {
//        Analytics analytics =mock(Analytics.class);
//        Map<String, Object> attributionData = null;
//
//        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
//        conversionListener.trackInstallAttributed(attributionData);
//
//        Map<String, Object> campaign = new ValueMap() //
//                .putValue("source", "")
//                .putValue("name", "")
//                .putValue("ad_group", "");
//        Properties properties = new Properties().putValue("provider", "AppsFlyer");
//        properties.putAll(attributionData);
//        properties.remove("media_source");
//        properties.remove("adgroup");
//        properties.putValue("campaign", campaign);
//        verify(analytics,times(1)).track("Install Attributed", properties);
//        reset(analytics);
//    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getFlag_happyFlow() throws Exception {
        String key="key";
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.getBoolean(key,false)).thenReturn(true);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        Method getFlagMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod("getFlag",String.class);
        getFlagMethod.setAccessible(true);

        boolean resBoolean = (Boolean) getFlagMethod.invoke(conversionListener,key);

        Assert.assertTrue(resBoolean==true);

        reset(analytics,app,context,sharedPreferences,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_setFlag_happyFlow() throws Exception {
        String key="key";
        boolean value=true;
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        when(context.getSharedPreferences("appsflyer-segment-data",0)).thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(editor);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        Method setFlagMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod("setFlag",String.class,boolean.class);
        setFlagMethod.setAccessible(true);

        setFlagMethod.invoke(conversionListener,key,value);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD){
            verify(editor,times(1)).apply();
        }
        else{
            verify(editor,times(1)).commit();
        }

        reset(analytics,app,context,sharedPreferences,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_editorCommit_happyFlow() throws Exception {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        Analytics analytics = mock(Analytics.class);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        Method editorCommitMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod("editorCommit",SharedPreferences.Editor.class);
        editorCommitMethod.setAccessible(true);

        editorCommitMethod.invoke(conversionListener,editor);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            verify(editor,times(1)).apply();
        }
        else{
            verify(editor,times(1)).commit();
        }

        reset(analytics,conversionListener);
    }

//    Not checking for editor is null.
//    @Test
//    public void testAppsflyerIntegration_ConversionListener_editorCommit_nullFlow() throws Exception {
//
//    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getContext_happyFlow() throws Exception {
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = mock(Context.class);
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        Method getContextMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod("getContext");
        getContextMethod.setAccessible(true);

        Context resContext = (Context) getContextMethod.invoke(conversionListener);

        Assert.assertTrue(resContext==context);

        reset(analytics,app,context,conversionListener);
    }

    @Test
    public void testAppsflyerIntegration_ConversionListener_getContext_nullFlow() throws Exception {
        Analytics analytics = mock(Analytics.class);
        Application app = mock(Application.class);
        Context context = null;
        when(analytics.getApplication()).thenReturn(app);
        when(app.getApplicationContext()).thenReturn(context);
        AppsflyerIntegration.ConversionListener conversionListener = spy(new AppsflyerIntegration.ConversionListener(analytics));
        Method getContextMethod = AppsflyerIntegration.ConversionListener.class.getDeclaredMethod("getContext");
        getContextMethod.setAccessible(true);

        Context resContext = (Context) getContextMethod.invoke(conversionListener);

        Assert.assertTrue(resContext==context);

        reset(analytics,app,conversionListener);
    }
}
