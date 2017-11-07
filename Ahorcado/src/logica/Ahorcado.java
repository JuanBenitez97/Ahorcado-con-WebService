/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Juan Benitez
 */
public class Ahorcado {

    
    private int id;

    JTextField campoPalabra;
    JLabel campoIntentos;
    JLabel campoErrores;
    JLabel largoPalabra;
    
    private boolean jugar = false;

    char[] palabra_secreta;
    private char[] palabra;

    int intentos = 0;
    boolean acerto = false;

    public Ahorcado() {
    }

    public Ahorcado(JTextField campoPalabra, String palabra, JLabel errores, JLabel campoIntentos, JLabel largoPalabra) {

        this.campoIntentos = campoIntentos;
        this.campoPalabra = campoPalabra;
        this.campoErrores = errores;
        this.largoPalabra = largoPalabra;
        
        //guardamos la palabra secreta
        this.palabra_secreta = palabra.toCharArray();

        String linea = "";

        //colocamos lineas segun el tamaño de la palabra
        for (int i = 0; i <= palabra_secreta.length - 1; i++) {
            linea += "_";
        }

        //convertimos la linea en vector de char y guardamos en la variable palabra
        this.palabra = linea.toCharArray();

        //colocamos las lineas a adivinar en el JtextField
        this.campoPalabra.setText(linea);

        //colocamos el icono de los intentos restantes
        this.campoIntentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/intentos/0.jpg")));

        //colocamos la imagen del ahorcado
        this.campoErrores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ahorcado/0.jpg")));

        this.jugar = true;

        this.largoPalabra.setText("La palabra tiene " + this.palabra_secreta.length + " letras.");

    }

    public String recuperarPalabra() throws IOException {
        String IP = "http://localhost:8080/api";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(IP);
        String palabra1 = "";
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity = response1.getEntity();
            if (entity != null) {
               InputStream instream = entity.getContent();
               String json = EntityUtils.toString(entity);
               System.out.println(json);
               palabra1 = new Gson().fromJson(json, String.class);
                //palabra1 = EntityUtils.toString(entity);
                //System.out.println(palabra1);

            }
            EntityUtils.consume(entity);
        } finally {
            response1.close();
        }

        System.out.println(palabra1);

        return palabra1;
    }

    public void validarPalabra(char letraIngresada) {
        if (jugar) {

            String letrasAcertadas = "";

            //validamos los intentos restantes
            if (this.intentos == 7) {

                this.campoErrores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ahorcado/8.jpg")));

                String respuesta = "";

                for (int i = 0; i <= this.palabra_secreta.length - 1; i++) {
                    respuesta = respuesta + this.palabra_secreta[i];
                }

                JOptionPane.showMessageDialog(null, "Lo siento perdiste, la palabra era " + respuesta, "Perdiste!", JOptionPane.ERROR_MESSAGE);
            } else {
                //evaluamos si ha adivinado la palabra comparando cada letra
                for (int i = 0; i <= palabra_secreta.length - 1; i++) {

                    //validamos que la letra este en la palabra
                    if (this.palabra_secreta[i] == letraIngresada) {
                        this.palabra[i] = letraIngresada;
                        this.acerto = true;
                    }

                    letrasAcertadas += this.palabra[i]; //guardamos la palabra con las letras acertadas

                }
                //si no acerto, mostramos el intento fallido y el ahorcado

                if (this.acerto == false) {

                    intentos += 1; //Numero de errores

                    campoIntentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/intentos/" + this.intentos + ".jpg")));
                    campoErrores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ahorcado/" + this.intentos + ".jpg")));

                    //Mostramos un mensaje avisando que erró la letra
                    if (intentos < 8) {
                        JOptionPane.showMessageDialog(null, "Fallaste! \n\n\t Te quedan " + ((8) - this.intentos) + " intentos.");
                    }

                } else {
                    this.acerto = false;
                }

                this.campoPalabra.setText(letrasAcertadas);
                //comprobamos el estado del juego

                validarGano();
            }
        }
    }

    private void validarGano() {

        boolean gano = false;

        for (int i = 0; i <= this.palabra_secreta.length - 1; i++) {
            if (this.palabra[i] == this.palabra_secreta[i]) {
                gano = true;
            } else {
                gano = false;
                break;
            }
        }

        if (gano) {
            JOptionPane.showMessageDialog(null, "!! Felicidades !! \n Adivinaste la palabra", "Ganaste", 0, new javax.swing.ImageIcon(getClass().getResource("/ahorcado/goals.png")));
        }
    }
}
