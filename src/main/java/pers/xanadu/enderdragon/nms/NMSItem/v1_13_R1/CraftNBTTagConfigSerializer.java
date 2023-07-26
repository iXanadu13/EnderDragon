package pers.xanadu.enderdragon.nms.NMSItem.v1_13_R1;

import com.mojang.brigadier.StringReader;
import net.minecraft.server.v1_13_R1.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class CraftNBTTagConfigSerializer {
    private static final Pattern ARRAY = Pattern.compile("^\\[.*]");
    private static final Pattern INTEGER = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)?i", 2);
    private static final Pattern DOUBLE = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    public static final MojangsonParser MOJANGSON_PARSER = new MojangsonParser(new StringReader(""));
    private static final Pattern byte_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern short_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern integer_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private static final Pattern long_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern float_format = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern double_format = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern double_format_2 = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);

    public CraftNBTTagConfigSerializer() {
    }

    public static Object serialize(NBTBase base) {
        if (base instanceof NBTTagCompound) {
            Map<String, Object> innerMap = new HashMap();
            Iterator var3 = ((NBTTagCompound)base).getKeys().iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();
                innerMap.put(key, serialize(((NBTTagCompound)base).get(key)));
            }

            return innerMap;
        } else if (!(base instanceof NBTTagList)) {
            if (base instanceof NBTTagString) {
                return base.b_();
            } else {
                return base instanceof NBTTagInt ? base + "i" : base.toString();
            }
        } else {
            List<Object> baseList = new ArrayList();

            for(int i = 0; i < ((NBTList)base).size(); ++i) {
                baseList.add(serialize(((NBTList)base).get(i)));
            }

            return baseList;
        }
    }

    public static NBTBase v1_13_R1_b(String str) {
        try {
            if (float_format.matcher(str).matches()) {
                return new NBTTagFloat(Float.parseFloat(str.substring(0, str.length() - 1)));
            }

            if (byte_format.matcher(str).matches()) {
                return new NBTTagByte(Byte.parseByte(str.substring(0, str.length() - 1)));
            }

            if (long_format.matcher(str).matches()) {
                return new NBTTagLong(Long.parseLong(str.substring(0, str.length() - 1)));
            }

            if (short_format.matcher(str).matches()) {
                return new NBTTagShort(Short.parseShort(str.substring(0, str.length() - 1)));
            }

            if (integer_format.matcher(str).matches()) {
                return new NBTTagInt(Integer.parseInt(str));
            }

            if (double_format.matcher(str).matches()) {
                return new NBTTagDouble(Double.parseDouble(str.substring(0, str.length() - 1)));
            }

            if (double_format_2.matcher(str).matches()) {
                return new NBTTagDouble(Double.parseDouble(str));
            }

            if ("true".equalsIgnoreCase(str)) {
                return new NBTTagByte((byte)1);
            }

            if ("false".equalsIgnoreCase(str)) {
                return new NBTTagByte((byte)0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new NBTTagString(str);
    }

    public static NBTBase deserialize(Object object) {
        if (object instanceof Map) {
            NBTTagCompound compound = new NBTTagCompound();
            for (Object obj : ((Map) object).entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry) obj;
                compound.set(entry.getKey(), deserialize(entry.getValue()));
            }
            return compound;
        } else if (!(object instanceof List)) {
            if (object instanceof String) {
                String string = (String)object;
                if (ARRAY.matcher(string).matches()) {
                    try {
                        Method parseArray = MojangsonParser.class.getDeclaredMethod("h");
                        parseArray.setAccessible(true);
                        MojangsonParser parser = new MojangsonParser(new StringReader(string));
                        return (NBTBase) parseArray.invoke(parser);
                    } catch (Throwable e) {
                        throw new RuntimeException("Could not deserialize found list ", e);
                    }
                } else if (INTEGER.matcher(string).matches()) {
                    return new NBTTagInt(Integer.parseInt(string.substring(0, string.length() - 1)));
                } else if (DOUBLE.matcher(string).matches()) {
                    return new NBTTagDouble(Double.parseDouble(string.substring(0, string.length() - 1)));
                } else {
                    try{
                        Method parseLiteral = MojangsonParser.class.getDeclaredMethod("b", String.class);
                        parseLiteral.setAccessible(true);
                        NBTBase nbtBase = (NBTBase) parseLiteral.invoke(MOJANGSON_PARSER,string);
                        if (nbtBase instanceof NBTTagInt) {
                            return new NBTTagString(nbtBase.b_());
                        } else {
                            return (nbtBase instanceof NBTTagDouble ? new NBTTagString(String.valueOf(((NBTTagDouble)nbtBase).asDouble())) : nbtBase);
                        }
                    }catch (ReflectiveOperationException e){
                        throw new RuntimeException("Could not deserialize NBTBase");
                    }
                }
            } else {
                throw new RuntimeException("Could not deserialize NBTBase");
            }
        } else {
            List<Object> list = (List)object;
            if (list.isEmpty()) {
                return new NBTTagList();
            } else {
                NBTTagList tagList = new NBTTagList();
                for (Object tag : list) {
                    tagList.add(deserialize(tag));
                }
                return tagList;
            }
        }
    }
}
