package nanang.application.id.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

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
        webView.setWebChromeClient(new DashboardFragment.MyWebViewClient());

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
        webView.setWebChromeClient(new DashboardFragment.MyWebViewClient());

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
    Context context;
    private void loadDetail() {
        user data_user = CommonUtilities.getLoginUser(getActivity().getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new IJavascriptHandler(), "cpjs");


        SharedPreferences prefs = webView.getContext().getSharedPreferences("TokenPref", MODE_PRIVATE);

        String token = prefs.getString("token", null);


        webView.loadUrl("file:///android_asset/html/dashboard.html?nama_desa="+CommonUtilities.toTitleCase(data_user.getDesa().toLowerCase())+"&token="+token);
    }

    final class IJavascriptHandler {
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
        public void gotoTracking() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).addAset();
                }
            });
        }

        @JavascriptInterface
        public void gotoLokal() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(9);
                }
            });
        }

        @JavascriptInterface
        public void gotoManageAset() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(5);
                }
            });
        }

        @JavascriptInterface
        public void gotoPengaturan() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(7);
                }
            });
        }

        @JavascriptInterface
        public void gotoEditDesa() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(10);
                }
            });
        }


        @JavascriptInterface
        public void gotoTentang() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(8);
                }
            });
        }

        @JavascriptInterface
        public void gotoLaporan() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((MainActivity) getActivity()).displayView(11);
                }
            });
        }
    }
}
