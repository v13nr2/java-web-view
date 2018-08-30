package nanang.application.id.model;

import java.io.Serializable;

public class user implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int id;  // -> iya direkam..
    String nama, email, photo,kecamatan, kabupaten, propinsi, desa, lokal, kades, sekdes, pengurus, alamatdesa;

    public user(int id, String nama, String email, String kecamatan, String kabupaten, String propinsi, String desa, String lokal, String kades, String sekdes, String pengurus, String alamatdesa, String photo) {
        this.id    = id;
        this.nama  = nama;
        this.email = email;
        this.photo = photo;
        this.kecamatan = kecamatan;
        this.kabupaten = kabupaten;
        this.propinsi = propinsi;
        this.desa = desa;
        this.lokal = lokal;
        this.kades = kades;
        this.sekdes = sekdes;
        this.pengurus = pengurus;
        this.alamatdesa = alamatdesa;
    }

    public int getId() {
        return this.id;
    }

    public String getNama() {
        return this.nama;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoto() {
        return this.photo;
    }

    public String getKecamatan() {
        return this.kecamatan;
    }

    public String getKabupaten() {
        return this.kabupaten;
    }

    public String getPropinsi() {
        return this.propinsi;
    }

    public String getDesa() {
        return this.desa;
    }

    public String getLokal() {
        return this.lokal;
    }

    public String getKades() {
        return this.kades;
    }

    public String getSekdes() {
        return this.sekdes;
    }

    public String getPengurus() {
        return this.pengurus;
    }

    public String getAlamatdesa() {
        return this.alamatdesa;
    }
}
