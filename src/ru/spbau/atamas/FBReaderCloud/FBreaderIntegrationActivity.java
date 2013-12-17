package ru.spbau.atamas.FBReaderCloud;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import org.geometerplus.android.fbreader.api.PluginApi;


public class FBreaderIntegrationActivity extends Activity {
        @Override
        public void onCreate(Bundle bundle) {
                super.onCreate(bundle);
                final Intent intent = getIntent();
                if ("android.fbreader.action.ADD_OPDS_CATALOG".equals(intent.getAction())) {
                        ArrayList<PluginApi.MenuActionInfo> actions =
                                intent.<PluginApi.MenuActionInfo>getParcelableArrayListExtra(
                                        PluginApi.PluginInfo.KEY
                                );
                        if (actions == null) {
                                actions = new ArrayList<PluginApi.MenuActionInfo>();
                        }
                        final String baseUrl = intent.getData().toString();
                        actions.add(new PluginApi.MenuActionInfo(
                                Uri.parse(baseUrl + "/startDriveApp"),
                                getText(R.string.startDriveApp).toString(),
                                3
                        ));
                        intent.putExtra(PluginApi.PluginInfo.KEY, actions);
                        if (!startNextMatchingActivity(intent)) {
                                setResult(1, intent);
                        }
                }
                finish();
        }
}
