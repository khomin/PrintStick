package project;

public class StickerAttributes {
    public String serial_number;
    public int serial_number_font_size;
    public int serial_number_x;
    public int serial_number_y;
    public String barcode_code;
    public  String qr_code;
    public boolean print_qr;
    public boolean print_serial_num;
    public boolean print_barcode;
    public int point_qr_x;
    public int point_qr_y;
    public int qr_height;
    public int qr_widht;
    public boolean barcode_rote;
    public int point_barcode_x;
    public int point_barcode_y;
    public int barcode_height;
    public int barcode_widht;
    byte[] bytea;

    public StickerAttributes() {
        barcode_rote = false;
        barcode_code = "";
        serial_number = "";
        serial_number_font_size = 0;
        print_serial_num = false;
        print_barcode = false;
        print_qr = false;
        serial_number_x = 0;
        serial_number_y = 0;
        qr_code = "";
        String path = "";
        boolean use_qr_code = false;
        int point_barcode_x = 0;
        int point_barcode_y = 0;
        int point_qr_x = 0;
        int point_qr_y = 0;
        int barcode_height = 0;
        int barcode_widht = 0;
        int qr_height = 0;
        int qr_widht = 0;
    }
}
