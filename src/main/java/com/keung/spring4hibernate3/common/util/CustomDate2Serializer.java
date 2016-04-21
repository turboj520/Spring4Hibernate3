package com.keung.spring4hibernate3.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;  
import java.util.Date;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;  
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomDate2Serializer extends JsonSerializer<Date>
{  
    @Override  
    public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {  
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");  
            String formattedDate = formatter.format(value);  
            jgen.writeString(formattedDate);  
    }  
}
