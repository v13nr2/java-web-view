/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nanang.application.id.iad;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.model.user;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.d("Registration id", token);
        user data = CommonUtilities.getLoginUser(getApplicationContext());
        if(data.getId()>0) {
            new prosesUpdateRegisterRegId(data, token).execute();
        }

        //send this registrationId to your server
        //Toast.makeText(context, "Register Reg ID: "+registrationId+" To Server!", Toast.LENGTH_SHORT).show();
    }


    class prosesUpdateRegisterRegId extends AsyncTask<String, Void, JSONObject> {

        user data;
        String registrationId;
        boolean success;
        String message;

        prosesUpdateRegisterRegId(user data, String registrationId) {
            this.data = data;
            this.registrationId = registrationId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return null; //ServerUtilities.register(getApplicationContext(), registrationId, data.getId(), CommonUtilities.getGuestId(getApplicationContext()));
        }

        @Deprecated
        @Override
        protected void onPostExecute(JSONObject result) {


            success = false;
            message = "Gagal melakukan proses take action. Cobalah lagi.";
            if(result!=null) {
                try {
                    success = result.isNull("success")?false:result.getBoolean("success");
                    message = result.isNull("message")?message:result.getString("message");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(!success) {
                new prosesUpdateRegisterRegId(data, registrationId).execute();
            }
        }
    }
}
