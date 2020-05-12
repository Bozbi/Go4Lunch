package com.sbizzera.go4lunch.list_fragment.utils;

import com.sbizzera.go4lunch.list_fragment.models.ListFragmentAdapterModel;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class LikesComparatorTest {

    private LikesComparator comparator = new LikesComparator();

    @Test
    public void testEquals() {
        ListFragmentAdapterModel model1 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model1.getLikesCount()).willReturn(10);
        ListFragmentAdapterModel model2 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model2.getLikesCount()).willReturn(10);

        assertEquals(0, comparator.compare(model1, model2));
    }

    @Test
    public void testLess() {
        ListFragmentAdapterModel model1 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model1.getLikesCount()).willReturn(10);
        ListFragmentAdapterModel model2 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model2.getLikesCount()).willReturn(11);

        assertEquals(1, comparator.compare(model1, model2));
    }

    @Test
    public void testGreater() {
        ListFragmentAdapterModel model1 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model1.getLikesCount()).willReturn(11);
        ListFragmentAdapterModel model2 = Mockito.mock(ListFragmentAdapterModel.class);
        BDDMockito.given(model2.getLikesCount()).willReturn(10);

        assertEquals(-1, comparator.compare(model1, model2));
    }


}