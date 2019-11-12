package com.farid.framework;

import android.content.Context;

public class example2 extends Model {
    Col lastName = new Col().setSequence(2);
    Col name = new Col().setSequence(1);
    Col lastColumn = new Col().setSequence(3);

    public example2(Context mContext ) {
        super(mContext, "example2");
    }

    @Override
    public boolean unAssigneFromModel() {
        return true;
    }
}
