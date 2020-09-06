package org.dynamic.authority.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultSecurityMetadataSourceSupport implements SecurityMetadataSourceSupport {
    @Override
    public List<RequestAuthority> getAllAuthority() {
        return Collections.emptyList();
    }
}
