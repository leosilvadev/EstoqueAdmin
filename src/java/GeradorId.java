import java.util.LinkedList;
        import java.util.concurrent.atomic.AtomicInteger;

        import org.zkoss.zk.ui.Component;
        import org.zkoss.zk.ui.Desktop;
        import org.zkoss.zk.ui.Executions;
        import org.zkoss.zk.ui.Page;
        import org.zkoss.zk.ui.ext.Scope;
        import org.zkoss.zk.ui.metainfo.ComponentInfo;
        import org.zkoss.zk.ui.sys.IdGenerator;
/**
 *
 * @author Ian Tsai (zanyking)
 *
 */
public class GeradorId implements IdGenerator {
    private static final AtomicInteger DESKTOP_ID_AINT = new AtomicInteger();
    private final IdGenerator innerIdGenerator;
    /**
     * key="USE_TEST_COMP_UUID_GENERATOR", valueType=boolean
     */
    public GeradorId(){
        String useTesting = System.getProperty("USE_TEST_COMP_UUID_GENERATOR");
        Boolean isUnderTesting = Boolean.FALSE;
        if(useTesting!=null){
            try{
                isUnderTesting = Boolean.parseBoolean(useTesting);
            }catch(Exception e){
                //ignore, nothing need to do.
            }
        }
        if(isUnderTesting){
            innerIdGenerator = new TestingIdGenerator();
        }else{
            innerIdGenerator = new ProductionIdGenerator();
        }
    }

    public String nextComponentUuid(Desktop arg0, Component arg1, ComponentInfo arg2) {
        return innerIdGenerator.nextComponentUuid(arg0, arg1, arg2);
    }

    public String nextDesktopId(Desktop arg0) {
        return innerIdGenerator.nextDesktopId(arg0);
    }

    public String nextPageUuid(Page arg0) {
        return innerIdGenerator.nextPageUuid(arg0);
    }

    private static final String CHAR = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final char[] CHAR_ARR =CHAR.toCharArray();

    private static String encodeRadix36(int in){
        StringBuffer sb = new StringBuffer(4);
        // almost impossible for a desktop to contain more than 10,000 components.
        int inp = in,k,i;
        do{
            k = inp /CHAR_ARR.length;
            i = inp % CHAR_ARR.length;
            sb.append(CHAR_ARR[i]);
            inp = k;
        }
        while(k>0);

        return sb.reverse().toString();
    }

    private static int getAndIncrement(Scope scope, String key){
        AtomicInteger aInt = (AtomicInteger) scope.getAttribute(key);
        if (aInt == null) {
            scope.setAttribute(key, aInt = new AtomicInteger());
        }
        int i = aInt.getAndIncrement();
        return i;
    }

    static class ProductionIdGenerator implements IdGenerator{
        private final static String ID_NUMBER = "zk_id_num";

        public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo compinfo) {
            String key = ID_NUMBER + comp.getClass().getSimpleName();
            int i = getAndIncrement(desktop, key);
            String compId = "zc_" + comp.getClass().getSimpleName() +"_"+ i;
            return compId;
        }
        public String nextDesktopId(Desktop desktop) {
            int i = DESKTOP_ID_AINT.getAndIncrement();
            String dtid = "Desktop_" + i;
            ((javax.servlet.http.HttpServletResponse)Executions.getCurrent().getNativeResponse()).setHeader("Desktop", dtid);
            return dtid;
        }
        public String nextPageUuid(Page page) {
            return null;
        }
    }//end of class

    static class TestingIdGenerator implements IdGenerator{
        private static final String KEY_GENERIC_SHORT_NAME = "KEY_GENERIC_SHORT_NAME";
        private final static String KEY_UUID_ATOMIC_INT = "KEY_UUID_ATOMIC_INT";
        /*
         * (non-Javadoc)
         * @see org.zkoss.zk.ui.sys.IdGenerator#nextComponentUuid(org.zkoss.zk.ui.Desktop, org.zkoss.zk.ui.Component)
         */
        public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo compinfo) {

            Page page = comp.getPage();
            Scope scope = page==null? desktop : page;
            String pageUuid = page==null? "pseudo_page" : page.getUuid();

            String prefix = enc(comp.getClass().getSimpleName(),
                    enc(pageUuid, "zc_", desktop, false), desktop, false);

            String uuid = prefix + encodeRadix36(getAndIncrement(scope, KEY_UUID_ATOMIC_INT));

            return uuid;
        }

        /*
         * (non-Javadoc)
         * @see org.zkoss.zk.ui.sys.IdGenerator#nextDesktopId(org.zkoss.zk.ui.Desktop)
         */
        public String nextDesktopId(Desktop desktop) {
            int i = DESKTOP_ID_AINT.getAndIncrement();
            String dtid = "Desktop_" + i;
            ((javax.servlet.http.HttpServletResponse)Executions.getCurrent().getNativeResponse()).setHeader("Desktop", dtid);
            return dtid;
        }
        /*
         * (non-Javadoc)
         * @see org.zkoss.zk.ui.sys.IdGenerator#nextPageUuid(org.zkoss.zk.ui.Page)
         */
        public String nextPageUuid(Page page) {
            return null;
        }
        /**
         *
         * @param name
         * @param prefix
         * @param scope
         * @return
         */
        private static String enc(String name, String prefix, Scope scope, boolean disable){
            if(disable){
                return prefix+name+"_";
            }
            StatefulIdEncoder idEncoder = (StatefulIdEncoder) scope.getAttribute(KEY_GENERIC_SHORT_NAME);

            if (idEncoder == null) {
                scope.setAttribute(KEY_GENERIC_SHORT_NAME,
                        idEncoder = new StatefulIdEncoder());
            }
            return prefix+idEncoder.encode(name)+"_";
        }

        /**
         * @author ytsai1
         */
        private static class StatefulIdEncoder{
            final LinkedList<String> store = new LinkedList<String>();
            /**
             * @param name
             * @return
             */
            public String encode(String name){
                if(name==null || name.isEmpty())
                    throw new IllegalArgumentException("StatefulIdEncoder can't encode a null or empty String: "+name);
                int counter = 0;
                int idx = -1;
                for(String temp : store){
                    if(temp.equals(name)){
                        idx = counter;
                        break;
                    }
                    counter++;
                }
                if(idx<0){//if not exist
                    store.add(name);
                    return encodeRadix36(counter);
                }else{// it's exist
                    return encodeRadix36(idx);
                }
            }
        }
    }
}