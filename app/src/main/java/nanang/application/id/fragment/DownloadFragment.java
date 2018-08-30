package nanang.application.id.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import nanang.application.id.iad.MainActivity;
import nanang.application.id.iad.R;
import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.model.user;

public class DownloadFragment extends Fragment  {

    public static Button btnProgressBar, BtnProgressPenggunaan, BtnProgressPenghapusan;
    public static ImageView my_image;
    private static String server_laporan = "http://asetkonawe.lpkpd.org/";
    private static EditText tahun;

    //e rikut nama url dan nama hasil downloadnya
    //yge
    private static String file_url = server_laporan + "services_pdf_buku_inventaris_pdf.php";
    private static String file_url_penggunaan = server_laporan + "service_cetak_status_penggunaan_aset_pdf.php";
    private static String file_url_hapus= server_laporan + "service_penghapusan.php";
    public String temporari;

    //private static String file_url = "http://aset.lpkpd.org/smc/file/1495629133443.jpg";



    //http://aset.lpkpd.org/services_pdf_buku_inventaris_pdf.php?id=218&sampai=2017

    public String getTemporari() {
        return temporari;
    }

    public void setTemporari(String temporari) {
        this.temporari = temporari;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.download_fragment, container, false);
        btnProgressBar = (Button) rootView.findViewById(R.id.btnProgressBar);
        BtnProgressPenggunaan = (Button) rootView.findViewById(R.id.btnProgressBarPenggunaan);
        BtnProgressPenghapusan = (Button) rootView.findViewById(R.id.btnAsetHapus);
        my_image = (ImageView) rootView.findViewById(R.id.my_image);
        tahun = (EditText) rootView.findViewById(R.id.txtTahun);

        btnProgressBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                //bikin class public di Main Activity biar bisa panggil class ini.
                //new DownloadFileFromURL().execute(file_url);
                //((MainActivity) getActivity()).DownloadFileFromURL(file_url);
                user login = CommonUtilities.getLoginUser(getActivity().getApplicationContext());
                //String tahun = "2017";
                file_url+="?id="+login.getId()+"&sampai="+tahun.getText();
                temporari="services_pdf_buku_inventaris_"+tahun.getText()+"_"+login.getDesa()+".pdf";

                ((MainActivity) getActivity()).DownloadFileFromURL(temporari, file_url);
            }
        });

        BtnProgressPenggunaan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { //
                // starting new Async Task
                //bikin class public di Main Activity biar bisa panggil class ini.
                //new DownloadFileFromURL().execute(file_url);
                user login = CommonUtilities.getLoginUser(getActivity().getApplicationContext());

                file_url_penggunaan+="?id="+login.getId()+"&Sampai="+tahun.getText();
                temporari="Penggunaan_aset_"+tahun.getText()+"_"+login.getDesa()+".pdf";
                ((MainActivity) getActivity()).DownloadFileFromURL(temporari, file_url_penggunaan);
            }
        });

        BtnProgressPenghapusan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { //
                // starting new Async Task
                //bikin class public di Main Activity biar bisa panggil class ini.
                //new DownloadFileFromURL().execute(file_url);
                user login = CommonUtilities.getLoginUser(getActivity().getApplicationContext());

                file_url_hapus+="?id="+login.getId();
                temporari="Penghapusan_aset_"+tahun.getText()+"_"+login.getDesa()+".pdf";
                ((MainActivity) getActivity()).DownloadFileFromURL(temporari, file_url_hapus);
            }
        });

        return rootView;
    }

    //classnya taruh di Main ACtivity.
}