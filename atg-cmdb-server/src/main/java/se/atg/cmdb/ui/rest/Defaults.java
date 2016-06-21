package se.atg.cmdb.ui.rest;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class Defaults {

  public static final String MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";
  public static final String META_TYPE_HTML = MediaType.TEXT_HTML + ";charset=utf-8";
  public static final ToStringStyle STYLE = new ToStringStyle() {

        private static final long serialVersionUID = 1L;
        {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }

        /*
         * Override to not append null fields
         */
        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (value != null) {
                super.append(buffer, fieldName, value, fullDetail);
            }
        }

        /**
         * <p>Ensure <code>Singleton</ode> after serialization.</p>
         * @return the singleton
         */
        private Object readResolve() {
            return STYLE;
        }
    };
}
