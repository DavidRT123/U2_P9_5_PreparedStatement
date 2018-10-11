/*
 * Basándote en el ejercicio 4, crea otro programa, llámalo U2_P9_5_PreparedStatement para obtener
 * lo mismo que en el ejercicio 4 pero previamente se le pedirá al usuario qué base de datos quiere
 * utilizar (la SQLite, Derby o HSQLDB), y los datos se sacarán de dicha base de datos
 */
package u2_p9_5_preparedstatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mdfda
 */
public class U2_P9_5_PreparedStatement {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dept_no = args[0], engine = "", data = "", resp, user = "", pass = "", sql;

        //Para capturar la eleección del user
        Scanner sc = new Scanner(System.in);

        System.out.println("Elige BBDD:");
        System.out.println("1. SQLITE");
        System.out.println("2. DERBY");
        System.out.println("3. HSQLDB");
        System.out.println("********************");
        resp = sc.next();

        //Filtro para evitar que se introduzcan carácteres no válidos
        if (!resp.equals("1") && !resp.equals("2") && !resp.equals("3")) {
            System.err.println("INTRODUCE UN VALOR ENTRE 1 y 3");
        }

        switch (resp) {
            case "1":
                engine = "org.sqlite.JDBC";
                data = "jdbc:sqlite:C:\\Users\\mdfda\\Desktop\\DAM\\Acceso a Datos (AD)\\Tema 2\\Ejercicios\\clase\\bases\\sqlite\\ejemplo.db";
                break;
            case "2":
                engine = "org.apache.derby.jdbc.EmbeddedDriver";
                data = "jdbc:derby:C:\\Users\\mdfda\\Desktop\\DAM\\Acceso a Datos (AD)\\Tema 2\\Ejercicios\\clase\\bases\\derby\\ejemplo";
                break;
            case "3":
                engine = "org.hsqldb.jdbc.JDBCDriver";
                data = "jdbc:hsqldb:C://hsqldb-2.4.1//hsqldb//hsqldb//ejemplo1";
                user = "SA";
                pass = "";
                break;
        }

        try {
            Class.forName(engine);

            Connection con = DriverManager.getConnection(data, user, pass);

            sql = "SELECT DEPARTAMENTOS.DNOMBRE, (SELECT MAX(SALARIO) FROM PROFESORES WHERE DEPT_NO = ?) AS MAX_SALARIO, (SELECT COUNT(NOMBRE) FROM PROFESORES WHERE dept_no = ?) AS NUM_PROFESORES FROM DEPARTAMENTOS LEFT JOIN PROFESORES ON DEPARTAMENTOS.DEPT_NO = PROFESORES.DEPT_NO WHERE DEPARTAMENTOS.DEPT_NO = ?";

            PreparedStatement pS = con.prepareStatement(sql);

            pS.setInt(1, Integer.parseInt(dept_no));
            pS.setInt(2, Integer.parseInt(dept_no));
            pS.setInt(3, Integer.parseInt(dept_no));

            ResultSet result = pS.executeQuery();

            if (!result.next()) {
                System.out.println("El departamento no existe");
            } else {
                if (result.getInt("NUM_PROFESORES") == 0) {
                    System.out.println("El departamento " + result.getString("DNOMBRE") + " no tiene profesores");
                } else {
                    System.out.println("El departamento " + result.getString("DNOMBRE") + " tiene " + result.getInt("NUM_PROFESORES") + " profesores con un salario máximo de: " + result.getFloat("MAX_SALARIO"));
                }
            }
            
            result.close();
            pS.close();
            con.close();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(U2_P9_5_PreparedStatement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.out.println("Código de error: " + ex.getErrorCode());
            System.out.println("Mensaje de error: " + ex.getMessage());
            System.out.println("Estado SQL: " + ex.getSQLState());
        }
    }

}
