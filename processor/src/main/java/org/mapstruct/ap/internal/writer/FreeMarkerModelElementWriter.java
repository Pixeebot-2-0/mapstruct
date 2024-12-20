/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.writer;

import java.io.Writer;
import java.util.Map;

import org.mapstruct.ap.internal.writer.Writable.Context;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Delegate for writing given {@link Writable}s into a {@link Writer} using
 * FreeMarker templates. Any parameters passed to the
 * {@link ModelIncludeDirective} in addition to element itself can be accessed
 * from within the template using the {@code ext} pseudo-element.
 *
 * @author Gunnar Morling
 */
public class FreeMarkerModelElementWriter {

    public void write(FreeMarkerWritable writable, Context context, Writer writer) throws Exception {
        Configuration configuration = context.get( Configuration.class );
        Template template = configuration.getTemplate( writable.getTemplateName() );
        template.process(
            new ExternalParamsTemplateModel(
                new BeanModel( writable, BeansWrapper.getDefaultInstance() ),
                new SimpleMapModel( context.get( Map.class ), BeansWrapper.getDefaultInstance() )
            ),
            writer
        );
    }

    private static class ExternalParamsTemplateModel implements TemplateHashModel {

        private final BeanModel object;
        private final SimpleMapModel extParams;

        ExternalParamsTemplateModel(BeanModel object, SimpleMapModel extParams) {
            this.object = object;
            this.extParams = extParams;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if ( "ext".equals (key ) ) {
                return extParams;
            }
            else {
                return object.get( key );
            }
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return object.isEmpty() && extParams.isEmpty();
        }
    }
}
