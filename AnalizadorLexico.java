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
        char caracter;
        do
        {
            caracter = this.leer();
        } while(caracter == ' ' || caracter == '\t');

        if(caracter == 15)
            return new Token(Token.EOF);

        StringBuilder lexema = new StringBuilder(caracter + "");
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
            else if(this.esFinal(nuevo))
            {
                this.devolverCaracteres(nuevo, lexema);
                return this.devolverToken(nuevo, lexema);
            }
            else
            {
                estado = nuevo;
                caracter = this.leer();

                lexema.append(caracter);

                if (nuevo == 0)
                {
                    lexema = new StringBuilder(caracter + "");
                }
            }
        } while (true);


    }

    private Token devolverToken(int estado, StringBuilder lexema)
    {
        return switch (estado)
        {
            case 4 -> new Token(Token.PARI, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 5 -> new Token(Token.PARD, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 6 -> new Token(Token.COMA, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 7 -> new Token(Token.AMP, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 8 -> new Token(Token.LLAVEI, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 9 -> new Token(Token.LLAVED, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 10 -> new Token(Token.ASIG, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 11 -> new Token(Token.PYC, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 12 -> new Token(Token.OPAS, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 14 -> new Token(Token.ENTERO, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 17 -> new Token(Token.REAL, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            case 19 -> switch (lexema.toString())
            {
                case "float" -> new Token(Token.FLOAT, this.fila, this.columna - lexema.toString().length(), lexema.toString());
                case "int" -> new Token(Token.INT, this.fila, this.columna - lexema.toString().length(), lexema.toString());
                case "if" -> new Token(Token.IF, this.fila, this.columna - lexema.toString().length(), lexema.toString());
                default -> new Token(Token.ID, this.fila, this.columna - lexema.toString().length(), lexema.toString());
            };
            default -> null;
        };
    }

    private char leer()
    {
        try
        {
            char c;
            do{
                c = (char)this.raf.readByte();
                this.columna++;

                if(c == '\n')
                {
                    this.fila++;
                    this.columna = 1;
                }
            } while (c == '\n');

            return c;
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
                if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') return 18;
                else return 19;
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

    private void devolverCaracteres(int estado, StringBuilder lexema)
    {
        try
        {
            switch(estado)
            {
                case 14:
                case 17:
                case 19:
                    this.raf.seek(this.raf.getFilePointer() - 1);
                    lexema.setLength(lexema.length() - 1);
                    columna--;
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
