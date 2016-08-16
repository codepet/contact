package com.gc.contact.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRCodeUtil {

    public final static int QR_WIDTH = 300;
    public final static int QR_HEIGHT = 300;

    /**
     * 生成二维码
     *
     * @param data 二维码中包含的数据
     * @return 二维码Bitmap对象
     * @throws WriterException
     */
    public static Bitmap createQRImage(String data) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
        int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
        for (int y = 0; y < QR_HEIGHT; y++) {
            for (int x = 0; x < QR_WIDTH; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * QR_WIDTH + x] = 0xFF000000;
                } else {
                    pixels[y * QR_WIDTH + x] = 0xFFFFFFFF;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
        return bitmap;
    }

}
