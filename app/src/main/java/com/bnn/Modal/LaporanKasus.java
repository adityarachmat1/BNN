package com.bnn.Modal;

/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 10/31/2018.
 */
public class LaporanKasus {

    private String kasus_id, kasus_no;

    public LaporanKasus(String id, String no){
        this.kasus_id = id;
        this.kasus_no = no;
    }

    public String getId(){
        return kasus_id;
    }

    public String getNo(){
        return kasus_no;
    }

}
