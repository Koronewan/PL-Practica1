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
            default: return 0;
        }
    }

    private boolean esFinal(int estado)
    {
        return true;
    }

    private void devolverCaracteres(int estado)
    {
        try
        {
            switch(estado)
            {
                default:
                this.raf.seek(this.raf.getFilePointer() - 1);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
