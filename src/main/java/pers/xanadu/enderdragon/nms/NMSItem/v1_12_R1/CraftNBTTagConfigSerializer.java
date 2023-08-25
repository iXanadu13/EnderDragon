package pers.xanadu.enderdragon.nms.NMSItem.v1_12_R1;

import net.minecraft.server.v1_12_R1.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class CraftNBTTagConfigSerializer {
    private static final Pattern ARRAY = Pattern.compile("^\\[.*]");
    private static final Pattern INTEGER = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)?i", 2);
    private static final Pattern DOUBLE = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern byte_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern short_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern integer_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private static final Pattern long_format = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern float_format = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern double_format = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern double_format2 = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);


    public CraftNBTTagConfigSerializer() {
    }

    public static Object serialize(NBTBase base) {
        if (base instanceof NBTTagCompound) {
            Map<String, Object> innerMap = new HashMap();

            for (String key : ((NBTTagCompound) base).c()) {
                innerMap.put(key, serialize(((NBTTagCompound) base).get(key)));
            }

            return innerMap;
        }
        if (base instanceof NBTTagString) {
            return ((NBTTagString) base).c_();
        }
        else {
            return base instanceof NBTTagInt ? base + "i" : base.toString();
        }

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
                        Constructor<MojangsonParser> constructor = MojangsonParser.class.getDeclaredConstructor(String.class);
                        MojangsonParser parser = constructor.newInstance(string);
                        Method parseArray = MojangsonParser.class.getDeclaredMethod("k");
                        parseArray.setAccessible(true);
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
                        Constructor<MojangsonParser> constructor = MojangsonParser.class.getDeclaredConstructor(String.class);
                        MojangsonParser parser = constructor.newInstance("");
                        Method parseLiteral = MojangsonParser.class.getDeclaredMethod("c", String.class);
                        parseLiteral.setAccessible(true);
                        NBTBase nbtBase = (NBTBase) parseLiteral.invoke(parser,string);
                        if (nbtBase instanceof NBTTagInt) {
                            return new NBTTagString(nbtBase.toString());
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
            }
            else {
                NBTTagList tagList = new NBTTagList();
                for (Object tag : list) {
                    tagList.add(deserialize(tag));
                }
                return tagList;
            }
        }
    }
    public static NBTBase v1_12_R1_c(String raw) {
        try {
            if (float_format.matcher(raw).matches()) {
                return new NBTTagFloat(Float.parseFloat(raw.substring(0, raw.length() - 1)));
            }

            if (byte_format.matcher(raw).matches()) {
                return new NBTTagByte(Byte.parseByte(raw.substring(0, raw.length() - 1)));
            }

            if (long_format.matcher(raw).matches()) {
                return new NBTTagLong(Long.parseLong(raw.substring(0, raw.length() - 1)));
            }

            if (short_format.matcher(raw).matches()) {
                return new NBTTagShort(Short.parseShort(raw.substring(0, raw.length() - 1)));
            }

            if (integer_format.matcher(raw).matches()) {
                return new NBTTagInt(Integer.parseInt(raw));
            }

            if (double_format.matcher(raw).matches()) {
                return new NBTTagDouble(Double.parseDouble(raw.substring(0, raw.length() - 1)));
            }

            if (double_format2.matcher(raw).matches()) {
                return new NBTTagDouble(Double.parseDouble(raw));
            }

            if ("true".equalsIgnoreCase(raw)) {
                return new NBTTagByte((byte)1);
            }

            if ("false".equalsIgnoreCase(raw)) {
                return new NBTTagByte((byte)0);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new NBTTagString(raw);
    }
}
