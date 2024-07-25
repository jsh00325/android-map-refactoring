package campus.tech.kakao.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import campus.tech.kakao.map.data.history.History
import campus.tech.kakao.map.data.local_search.Location
import campus.tech.kakao.map.domain.usecase.GetHistoryUseCase
import campus.tech.kakao.map.domain.usecase.RemoveHistoryUseCase
import campus.tech.kakao.map.domain.usecase.SearchLocationUseCase
import campus.tech.kakao.map.domain.usecase.UpdateHistoryUseCase
import campus.tech.kakao.map.ui.search.SearchLocationViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class SearchLocationViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockGetHistoryUseCase = mockk<GetHistoryUseCase>()
    private val mockUpdateHistoryUseCase = mockk<UpdateHistoryUseCase>()
    private val mockRemoveHistoryUseCase = mockk<RemoveHistoryUseCase>()
    private val mockSearchLocationUseCase = mockk<SearchLocationUseCase>()
    private lateinit var viewModel: SearchLocationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        coEvery { mockGetHistoryUseCase() } returns emptyList()
        viewModel = SearchLocationViewModel(
            mockGetHistoryUseCase,
            mockUpdateHistoryUseCase,
            mockRemoveHistoryUseCase,
            mockSearchLocationUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun testInitViewModel() {
        // given
        val testHistory = listOf(History("기록1", 2), History("기록2", 1))
        val tempMockGetHistoryUseCase = mockk<GetHistoryUseCase>()
        coEvery { tempMockGetHistoryUseCase() } returns testHistory

        // when
        val tempViewModel = SearchLocationViewModel(
            tempMockGetHistoryUseCase,
            mockUpdateHistoryUseCase,
            mockRemoveHistoryUseCase,
            mockSearchLocationUseCase
        )

        // then
        coVerify { tempMockGetHistoryUseCase() }
        tempViewModel.history.observeForever(mockk<Observer<List<History>>>(relaxed = true))
        assertEquals(testHistory, tempViewModel.history.value)
    }

    @Test
    fun testSearchLocation() {
        // given
        val testLocation = listOf(
            Location("Location1", "Address1", "Category1", 123.0, 456.0),
            Location("Location2", "Address2", "Category2", 135.0, 246.0)
        )
        coEvery { mockSearchLocationUseCase(any()) } returns testLocation
        viewModel.location.observeForever(mockk<Observer<List<Location>?>>(relaxed = true))

        // when
        viewModel.searchLocation("testCategory")

        // then
        coVerify { mockSearchLocationUseCase("testCategory") }
        assertEquals(2, viewModel.location.value?.size)
        assertEquals(testLocation[0], viewModel.location.value?.get(0))
        assertEquals(testLocation[1], viewModel.location.value?.get(1))
    }

    @Test
    fun testAddHistory() {
        // given
        val testHistory = listOf(
            History("History1", 2), History("History2", 1)
        )
        coEvery { mockUpdateHistoryUseCase(any()) } just Runs
        coEvery { mockGetHistoryUseCase() } returns testHistory
        viewModel.history.observeForever(mockk<Observer<List<History>>>(relaxed = true))

        // when
        viewModel.addHistory("History2")

        // then
        coVerify { mockUpdateHistoryUseCase("History2") }
    }

    @Test
    fun testRemoveHistory() {
        // given
        val testHistory = listOf(History("History1", 1))
        coEvery { mockRemoveHistoryUseCase(any()) } just Runs
        coEvery { mockGetHistoryUseCase() } returns testHistory
        viewModel.history.observeForever(mockk<Observer<List<History>>>(relaxed = true))

        // when
        viewModel.removeHistory("History2")

        // then
        coVerify { mockRemoveHistoryUseCase("History2") }
    }

    @Test
    fun testAddMarker() {
        // given
        val testLocation = Location("name", "address", "category", 1.0, 2.0)
        viewModel.markerLocation.observeForever(mockk<Observer<Location>>(relaxed = true))

        // when
        viewModel.addMarker(testLocation)

        // then
        assertEquals(testLocation, viewModel.markerLocation.value)
    }

    @Test
    fun testSearchLocationByHistory() {
        // given
        val testHistoryName = "testHistory"
        viewModel.searchInput.observeForever(mockk<Observer<String>>(relaxed = true))

        // when
        viewModel.searchLocationByHistory(testHistoryName)

        // then
        assertEquals(testHistoryName, viewModel.searchInput.value)
    }
}