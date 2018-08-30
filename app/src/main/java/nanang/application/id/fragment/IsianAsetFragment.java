package nanang.application.id.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import nanang.application.id.iad.MainActivity;
import nanang.application.id.iad.R;
import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.model.user;
import nanang.application.id.semutsoft.LocationHelper;

public class IsianAsetFragment extends Fragment {

    public static WebView webView;
    public static RelativeLayout load_masking;
    WebSettings webSettings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_html, container, false);

        webView = (WebView) rootView.findViewById(R.id.webView);
        load_masking = (RelativeLayout) rootView.findViewById(R.id.loadmasking);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebChromeClient(new MyWebViewClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webView.setWebChromeClient(new MyWebViewClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        loadDetail();
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress==100) {

            }
            super.onProgressChanged(view, newProgress);
        }
    }

    public void loadDetail() {
        user data_user = CommonUtilities.getLoginUser(getActivity().getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new IJavascriptHandler(), "cpjs2");
        //v13nr
        final LocationHelper locationHelper;
        double lat, lng;
        locationHelper = new LocationHelper(getActivity());
       // if (locationHelper.isCanGetLocation()) {
        //    if (locationHelper != null) {



                lat = locationHelper.getLat();
                lng = locationHelper.getLng();

           // }
       // }
        webView.loadUrl("file:///android_asset/html/isian.html?nama_desa=mks&lat="+lat+"&lng="+lng);
    }

    public class IJavascriptHandler {
        IJavascriptHandler() {
        }

        @JavascriptInterface
        public void gotoLogout() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).openDialogLogout();
                }
            });
        }

        @JavascriptInterface
        public void gotoSaveAset2(final String jenisbarang, final String lat, final String lng) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
                    ((MainActivity) getActivity()).prosesSaveAset(jenisbarang, lat, lng);

                }
            });
        }

        @JavascriptInterface
        public void gotoBack() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    ((MainActivity) getActivity()).displayView(MainActivity.action_add.equalsIgnoreCase("lokal")?9:3);
                }
            });
        }

        @JavascriptInterface
        public void cekAction() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    webView.loadUrl("javascript:setTitle('"+MainActivity.action_add+"');");
                }
            });
        }
    }
}
