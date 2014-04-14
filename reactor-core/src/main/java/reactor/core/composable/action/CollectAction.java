/*
 * Copyright (c) 2011-2013 GoPivotal, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.composable.action;

import reactor.event.dispatch.Dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephane Maldini
 * @since 1.1
 */
public class CollectAction<T> extends BatchAction<T, List<T>> implements Flushable<T> {

	private final List<T> values;

	public CollectAction(int batchsize, Dispatcher dispatcher, ActionProcessor<List<T>> actionProcessor) {
		super(batchsize, dispatcher, actionProcessor, true, false, batchsize > 0);
		values = new ArrayList<T>(batchsize > 0 ? batchsize : 256);
	}

	@Override
	public void nextCallback(T value) {
		values.add(value);
	}

	@Override
	public void flushCallback(T ev) {
		if (values.isEmpty()) {
			return;
		}
		output.onNext(new ArrayList<T>(values));
		values.clear();
	}

	@Override
	public Flushable<T> flush() {
		lock.lock();
		try {
			flushCallback(null);
		} finally {
			lock.unlock();
		}
		return this;
	}

}
