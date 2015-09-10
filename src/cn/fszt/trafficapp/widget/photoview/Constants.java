/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package cn.fszt.trafficapp.widget.photoview;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants {

//	public static final String[] IMAGES = new String[] {
//			// Heavy images
//			"http://img.anzhuo.im/public/picture/2012122401/1348137920992.jpg",
//			"http://img6.3lian.com/c23/desk2/18/15/1935.jpg",
//			"http://img.wz6.org/uploads/allimg/120423/1-120423132210.jpg",
//			"http://pic17.nipic.com/20111026/3631203_111238646000_2.jpg"// Wrong link
//	};
	
	public static List<String> IMAGES = new ArrayList<String>();
	

	private Constants() {
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
}
