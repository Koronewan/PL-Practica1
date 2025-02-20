import java.io.IOException;
import java.io.RandomAccessFile;

public class AnalizadorLexico
{
    private RandomAccessFile raf;

    public AnalizadorLexico(RandomAccessFile entrada)
    {
        this.raf = entrada;
    }

    public Token siguienteToken()
    {
        Token token = new Token();
        char caracter = this.leer();

        return token;
    }

    private char leer()
    {
        try{
            return this.raf.readChar();
        }
        catch(IOException e)
        {
            return '\0';
        }

    }
}
