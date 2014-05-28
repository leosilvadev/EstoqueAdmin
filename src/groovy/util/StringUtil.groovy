package util

import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Created by leonardo on 17/02/14.
 */
class StringUtil {

    static String formataPadraoDinheiro(double valor){
        def format = new DecimalFormat()
        def symbols = format.decimalFormatSymbols
        symbols.groupingSeparator = ','
        format.decimalFormatSymbols = symbols
        return "R\$ ${format.format(valor)}"
    }

    static String gerarStringDataHora(String formato){
        SimpleDateFormat df = new SimpleDateFormat(formato)
        return df.format(Calendar.getInstance().time)
    }
    
}
