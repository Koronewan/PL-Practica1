import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AnalizadorLexico
{
    private RandomAccessFile raf;
    private int fila = 1;
    private int columna = 1;

    public AnalizadorLexico(RandomAccessFile entrada)
    {
        this.raf = entrada;
    }

    public Token siguienteToken()
    {
        char caracter = this.leer();
        int estado = 0;

        do
        {
            int nuevo = delta(estado, caracter);
            if(nuevo == -2)
            {
                System.out.println("Error lexico: fin de fichero inesperado");
                System.exit(-1);
            }
            else if(nuevo == -1)
            {
                System.out.println("Error lexico (" + this.fila + "," + this.columna
                        + "): caracter '" + caracter + "' incorrecto");

                System.exit(-1);
            }
            else if(this.esFinal(estado))
            {
                devolverCaracteres(estado);
                return new Token();
            }
            else
            {
                estado = nuevo;
                caracter = this.leer();
                this.columna++;

                if(caracter == '\n')
                {
                    this.fila++;
                    this.columna = 1;
                    caracter = this.leer();
                }
            }
        } while (true);


    }

    private char leer()
    {
        try{
            return (char)this.raf.read();
        }
        catch (EOFException e)
        {
            return Token.EOF;
        }
        catch(IOException e)
        {
            return '\0';
        }

    }

    private int delta(int estado, int c)
    {
        switch(estado)
        {
            case 0:
                if(c == '/') return 1;
                else if(c == '(') return 4;
                else if(c == ')') return 5;
                else if(c == ',') return 6;
                else if(c == '&') return 7;
                else if(c == '{') return 8;
                else if(c == '}') return 9;
                else if(c == '=') return 10;
                else if(c == ';') return 11;
                else if(c == '-' || c == '+') return 12;
                else if(c >= '0' && c <= '9') return 13;
                else if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') return 18;
                return -1;
            case 1:
                if(c == '*') return 2;
                else return -1;
            case 2:
                if(c == '*') return 3;
                else return 2;
            case 3:
                if(c == '/') return 0;
                else return 2;
            case 13:
                if(c >= '0' && c <= '9') return 13;
                else if(c == '.') return 15;
                else return 14;
            case 15:
                if(c >= '0' && c <= '9') return 16;
                else return -1;
            case 16:
                if(c >= '0' && c <= '9') return 16;
                else return 17;
            case 18:
                if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') return 19;
                else return -1;
            default:
                return -1;
        }
    }

    private boolean esFinal(int estado)
    {
        return estado == 4 || estado == 5 || estado == 6 || estado == 7 || estado == 8
                || estado == 9 || estado == 10 || estado == 11 || estado == 12
                || estado == 14 || estado == 17 || estado == 19;
    }

    private void devolverCaracteres(int estado)
    {
        try
        {
            switch(estado)
            {
                case 14:
                case 17:
                case 19:
                    this.raf.seek(this.raf.getFilePointer() - 1);
                    break;
                default:
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
