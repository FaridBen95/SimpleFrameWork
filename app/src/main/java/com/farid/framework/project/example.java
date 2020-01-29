package com.farid.framework.project;

import android.content.Context;

import com.farid.framework.framework_repository.core.Col;
import com.farid.framework.framework_repository.core.Model;

public class example extends Model {
    Col lastName = new Col();

    public example(Context mContext ) {
        super(mContext, "example");
    }

}
