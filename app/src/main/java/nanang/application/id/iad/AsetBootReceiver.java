package nanang.application.id.iad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.libs.DatabaseHandler;
import nanang.application.id.model.aset;

public class AsetBootReceiver extends BroadcastReceiver {
    private static final String TAG = "AsetBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        int total_data = new DatabaseHandler(context).getRowCount("aset");
        if (total_data>0) {
            CommonUtilities.setCurentlyTracking(context, true);
            Intent i = new Intent(context, AsetService.class);
            i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(i);
        }
    }
}
