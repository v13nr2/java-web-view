package nanang.application.id.model;

import java.io.Serializable;

public class aset implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int id;
    String jenisbarang, kodebarang, identitasbarang, gambar;

    public aset(int id, String jenisbarang, String kodebarang, String identitasbarang, String gambar) {
        this.id    = id;
        this.jenisbarang  = jenisbarang;
        this.kodebarang = kodebarang;
        this.identitasbarang = identitasbarang;
        this.gambar = gambar;
    }

    public int getId() {
        return this.id;
    }

    public String getJenisbarang() {
        return this.jenisbarang;
    }

    public String getKodebarang() {
        return this.kodebarang;
    }

    public String getIdentitasbarang() {
        return this.identitasbarang;
    }


    public String getGambar() {
        return this.gambar;
    }
}
