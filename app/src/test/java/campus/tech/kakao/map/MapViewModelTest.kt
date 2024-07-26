package campus.tech.kakao.map

import android.util.Log
import campus.tech.kakao.map.domain.usecase.LoadLastLocationUseCase
import campus.tech.kakao.map.domain.usecase.SaveLastLocationUseCase
import campus.tech.kakao.map.ui.map.MapViewModel
import com.kakao.vectormap.LatLng
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

@ExperimentalCoroutinesApi
class MapViewModelTest {
    private val mockSaveLastLocationUseCase = mockk<SaveLastLocationUseCase>()
    private val mockLoadLastLocationUseCase = mockk<LoadLastLocationUseCase>()
    private val viewModel = MapViewModel(mockSaveLastLocationUseCase, mockLoadLastLocationUseCase)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSaveLastLocation() {
        // given
        coEvery { mockSaveLastLocationUseCase(any(), any()) } just Runs
        val latitude = 123.0
        val longitude = 456.0

        // when
        viewModel.saveLastLocation(latitude, longitude)

        // then
        coVerify { mockSaveLastLocationUseCase(latitude, longitude) }
    }

    @Test
    fun testSaveLastLocation_IOException() {
        // given
        coEvery { mockSaveLastLocationUseCase(any(), any()) } throws IOException("Test Exception")
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        val latitude = 123.0
        val longitude = 456.0

        // when
        viewModel.saveLastLocation(latitude, longitude)

        // then
        coVerify { mockSaveLastLocationUseCase(latitude, longitude) }
        verify { Log.e("MapViewModel", "Failed to save location", any()) }
    }

    @Test
    fun testLoadLastLocation_ValueExist() {
        // given
        val testLatLng = LatLng.from(35.89053, 128.6118)
        coEvery { mockLoadLastLocationUseCase() } returns flowOf(testLatLng)
        val testCallBack: (Double, Double, Boolean) -> Unit = { latitude, longitude, _ ->
            assertEquals(testLatLng.latitude, latitude, 1e-5)
            assertEquals(testLatLng.longitude, longitude, 1e-5)
        }

        // when
        viewModel.loadLastLocation(testCallBack)

        // then
        coVerify { mockLoadLastLocationUseCase() }
    }

    @Test
    fun testLoadLastLocation_NotExist() {
        // given
        coEvery { mockLoadLastLocationUseCase() } returns flowOf(null)
        val mockCallBack = mockk<(Double, Double, Boolean) -> Unit>()
        coEvery { mockCallBack(any(), any(), any()) } just Runs

        // when
        viewModel.loadLastLocation(mockCallBack)

        // then
        coVerify { mockLoadLastLocationUseCase() }
        coVerify(exactly = 0) { mockCallBack(any(), any(), any()) }
    }
}