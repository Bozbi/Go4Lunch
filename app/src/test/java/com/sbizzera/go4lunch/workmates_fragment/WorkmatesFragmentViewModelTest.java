package com.sbizzera.go4lunch.workmates_fragment;

import android.content.Context;
import android.graphics.Typeface;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.sbizzera.go4lunch.LiveDataTestUtil;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.repositories.firestore.FireStoreRepo;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreLunch;
import com.sbizzera.go4lunch.repositories.firestore.models.FireStoreUser;
import com.sbizzera.go4lunch.workmates_fragment.models.WorkmatesFragmentAdapterModel;
import com.sbizzera.go4lunch.workmates_fragment.models.WorkmatesFragmentModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkmatesFragmentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Context context;

    private WorkmatesFragmentViewModel viewModel;

    private MutableLiveData<List<FireStoreUser>> fireStoreUsersLD = new MutableLiveData<>();
    private MutableLiveData<List<FireStoreLunch>> allTodaysLunchesLD = new MutableLiveData<>();

    @Before
    public void setUp() {
        FireStoreRepo fireStoreRepo = mock(FireStoreRepo.class);
        doReturn(fireStoreUsersLD).when(fireStoreRepo).getAllUsers();
        doReturn(allTodaysLunchesLD).when(fireStoreRepo).getAllTodaysLunches();

        when(context.getString(R.string.eat_at)).thenReturn(" eats at ");
        when(context.getString(R.string.hasnt_decided)).thenReturn(" hasn't decided yet");

        viewModel = new WorkmatesFragmentViewModel(fireStoreRepo, context);
    }

    @Test
    public void getModelLiveData() throws InterruptedException {
        //given
        fireStoreUsersLD.setValue(mockAllusers());
        allTodaysLunchesLD.setValue(mockAllTodaysLunches());

        //when
        WorkmatesFragmentModel model = LiveDataTestUtil.getOrAwaitValue(viewModel.getModelLiveData());
        WorkmatesFragmentAdapterModel firstResult = model.getWorkmatesList().get(0);
        WorkmatesFragmentAdapterModel secondResult = model.getWorkmatesList().get(1);

        //then
        assertEquals(2, model.getWorkmatesList().size());

        assertEquals("www.testeur2photo.com", firstResult.getPhotoUrl());
        assertEquals("testeur2 eats at restName1", firstResult.getChoice());
        assertEquals(true, firstResult.getClickable());
        assertEquals(Typeface.BOLD, firstResult.getTextStyle());
        assertEquals("restId1", firstResult.getRestaurantId());

        assertEquals("www.testeur1photo.com", secondResult.getPhotoUrl());
        assertEquals("testeur1 hasn't decided yet", secondResult.getChoice());
        assertEquals(false, secondResult.getClickable());
        assertEquals(Typeface.ITALIC, secondResult.getTextStyle());
        assertNull(secondResult.getRestaurantId());
    }

    private List<FireStoreUser> mockAllusers() {
        return Arrays.asList(
                new FireStoreUser("id1", "testeur1", "www.testeur1photo.com"),
                new FireStoreUser("id2", "testeur2", "www.testeur2photo.com")
        );
    }

    private List<FireStoreLunch> mockAllTodaysLunches() {
        return Collections.singletonList(
                new FireStoreLunch("id2", "testeur2", "www.testeur2photo.com", "restId1", "restName1")
        );
    }
}