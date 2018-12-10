/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mail;

/**
 * 参考サイト
 * http://isotai.hatenablog.com/entry/2017/02/26/180910
 * 
 * java mail のダウンロード先（Java EE Platform Downloads）
 * https://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-eeplat-419426.html#javamail-1.4.7-oth-JPR
 * 
 * 
 * ＜＜前提条件 1＞＞
 * まずgmailの設定（歯車アイコン）からIMAP アクセスを有効にする。
 *
 * ＜＜前提条件 2＞＞
 * http://isotai.hatenablog.com/entry/2017/02/26/180910
 * 上記のサイトにもあるが、googleにログインしたあと、以下の 
 * https://myaccount.google.com/lesssecureapps
 * に於て「安全性の低いアプリのアクセス」を許可（ボタンを右に寄せる）をしな
 * ければブロックされて送信できない（例外が戻ってくる）。
 * 
 * ＜＜前提条件 2を行わなかった場合＞＞
 * 不審なアプリによるアカウントへのアクセスをブロックしました
 * masatotakainishiohi@gmail.com Google
 * 以外のアプリから誰かがあなたのアカウントにログインしようとしましたが、
 * ブロックされました。心当たりがない場合は、誰かにパスワードを知られています。
 * パスワードを今すぐ変更してください。
 *  不明な端末  30 分前  日本付近 160.16.104.37 （IP アドレス）
 * 最近、Google
 * アカウントへのログインをブロックされましたか？
 *などと警告がでる。
 * 
 * http://java.sakura.ne.jp/base/page-222は参考になるが動作しなかった。
 *
 * @author kyokuto
 */
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailtest {

//エンコード指定
    private static final String ENCODE = "ISO-2022-JP";

    public static void main(final String[] args) {

        //メール送付
        new SendMailtest().send(args[0], args[1]);

    }

    public static void invoke(String mailadd, String pass) {

        new SendMailtest().send(mailadd, pass);

    }
//ここからメール送付に必要なSMTP,SSL認証などの設定

    public void send(String mailadd, String pass) {
        final Properties props = new Properties();

        // SMTPサーバーの設定。ここではgooglemailのsmtpサーバーを設定。
        props.setProperty("mail.smtp.host", "smtp.gmail.com");

        // SSL用にポート番号を変更。
        props.setProperty("mail.smtp.port", "465");

        // タイムアウト設定
        props.setProperty("mail.smtp.connectiontimeout", "60000");
        props.setProperty("mail.smtp.timeout", "60000");

        // 認証
        props.setProperty("mail.smtp.auth", "true");

        // SSLを使用するとこはこの設定が必要。
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");

        //propsに設定した情報を使用して、sessionの作成
        final Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailadd, pass);
            }
        });

        // ここからメッセージ内容の設定。上記で作成したsessionを引数に渡す。
        final MimeMessage message = new MimeMessage(session);

        try {
            final Address addrFrom = new InternetAddress(
                    "autosend@gmail.com", "送信者の表示名", ENCODE);
            message.setFrom(addrFrom);

            final Address addrTo = new InternetAddress("zaf_docomo@docomo.ne.jp",
                    "受信者の表示名", ENCODE);
            message.addRecipient(Message.RecipientType.TO, addrTo);

            // メールのSubject
            message.setSubject("ありがとうメッセージ受信しました！", ENCODE);

            // メール本文。
            message.setText("こんにちは。", ENCODE);

            // その他の付加情報。
            message.addHeader("X-Mailer", "blancoMail 0.1");
            message.setSentDate(new Date());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // メール送信。
        try {
            Transport.send(message);
        } catch (AuthenticationFailedException e) {
            // 認証失敗
            e.printStackTrace();
        } catch (MessagingException e) {
            // smtpサーバへの接続失敗
            e.printStackTrace();

        }
    }
}
