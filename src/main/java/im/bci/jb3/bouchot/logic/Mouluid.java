package im.bci.jb3.bouchot.logic;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import com.buck.common.codec.Codec;
import com.buck.common.codec.CodecDecoder;
import com.buck.common.codec.CodecEncoder;

public class Mouluid {
    private static final SecureRandom numberGenerator = new SecureRandom();
    private static final CodecEncoder encoder = Codec.forName("Base32").newEncoder();
    private static final CodecDecoder decoder = Codec.forName("Base32").newDecoder();

    private int time;
    private int rand;

    public static Mouluid generateMouluid() {
        Mouluid mouluid = new Mouluid();
        mouluid.time = Seconds.secondsBetween(new DateTime(2016, 1, 1, 0, 0), DateTime.now()).getSeconds();
        mouluid.rand = numberGenerator.nextInt();
        return mouluid;
    }

    public static Mouluid fromString(String str) {
        Mouluid mouluid = new Mouluid();
        ByteBuffer bytes = ByteBuffer.wrap(decoder.decode(str.getBytes()));
        mouluid.time = bytes.getInt();
        mouluid.rand = bytes.getInt();
        return mouluid;
    }

    public String getFormattedTime() {
        return DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").print(new DateTime(2016, 1, 1, 0, 0).plusSeconds(time));
    }
    
    public int getRand() {
        return rand;
    }

    @Override
    public String toString() {
        String encoded = new String(encoder.encode(ByteBuffer.allocate(8).putInt(time).putInt(rand).array()));
        return encoded.replaceAll("=", "").toLowerCase();
    }
    
    public static final String legacyTimezoneId = "Europe/Paris";
    public static final DateTimeZone legacyTimeZone = DateTimeZone.forID(legacyTimezoneId);
     static {
         DateTimeZone.setDefault(DateTimeZone.forID(legacyTimezoneId));
     }

 /*   public static void main(String[] args) {
        
        System.out.println(DateTimeFormat.forPattern("yyyy/MM/dd#HH:mm:ss").print(DateTime.now()));
        
        System.out.println("generate");
        ArrayList<Mouluid> ids = new ArrayList<Mouluid>();
        for (int i = 0; i < 20; ++i) {
            Mouluid id = generateMouluid();
            System.out.println(id);
            ids.add(id);
        }
        System.out.println("read");
        for (Mouluid id : ids) {
            System.out.println(id.getFormattedTime() + " " + id.getRand());
        }
    }*/
}
