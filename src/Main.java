import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/*
*   Writen by Omer Faruk TEKGOZOGLU.
*   Gazi Üniversitesi - Teknoloji Fakültesi Elektrik&Elektronik Mühendisliği
*   HAVELSAN 2018-MART
*
*
* YENİ DATABASE GELDİĞİNDE DEĞİŞTİRİLMESİ GEREKENLER ConnectionSingleton.java 'da DB_NAME, ve çıktı dosyasının adı main'de alt kısımda Path path yazan yer ve SQLiteConnect
* java da FileWriter'daki dosya adı. FileWriter ve main'deki dosya adları aynı olmak zorunda.
*
 */
public class Main {
    public static void main(String [] args) throws SQLException, IOException {
        SQLiteConnect SQLiteConnection =  new SQLiteConnect();

        System.out.println("##############################################");
        System.out.println(".....Yaziyor.....");
        SQLiteConnection.doWrite();
        Path path = Paths.get("C:/Users/asd/Desktop/script_3.txt");
        Charset charset = StandardCharsets.UTF_8;
//Burada yazan content'in database ile bir alakası yok!
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("'null'", "NULL");
        Files.write(path, content.getBytes(charset));
        System.out.println("---------------TAMAMLANDI---------------");
    }

}
