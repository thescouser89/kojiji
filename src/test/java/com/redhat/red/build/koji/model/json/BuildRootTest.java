/**
 * Copyright (C) 2015 Red Hat, Inc. (jcasey@redhat.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.red.build.koji.model.json;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jdcasey on 2/16/16.
 */
public class BuildRootTest
    extends AbstractJsonTest
{

    @Test
    public void jsonRoundTrip()
            throws IOException, VerificationException
    {
        BuildRoot src = newBuildRoot();
        String json = mapper.writeValueAsString( src );
        System.out.println( json );

        BuildRoot out = mapper.readValue( json, BuildRoot.class );

        assertThat( out, equalTo( src ) );
    }

}
