/*
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import reactor.core.Scannable;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class MonoRetryPredicateTest {

	@SuppressWarnings("deprecation")
	@Test
	public void twoRetryNormalSupplier() {
		AtomicInteger i = new AtomicInteger();
		AtomicBoolean bool = new AtomicBoolean(true);

		StepVerifier.create(Mono.fromCallable(i::incrementAndGet)
		                        .doOnNext(v -> {
		                        	if(v < 4) {
				                        throw new RuntimeException("test");
			                        }
			                        else {
		                        		bool.set(false);
			                        }
		                        })
		                        .retry(3, e -> bool.get()))
		            .expectNext(4)
		            .expectComplete()
		            .verify();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void twoRetryErrorSupplier() {
		AtomicInteger i = new AtomicInteger();
		AtomicBoolean bool = new AtomicBoolean(true);

		StepVerifier.create(Mono.fromCallable(i::incrementAndGet)
		                        .doOnNext(v -> {
		                        	if(v < 4) {
		                        		if( v > 2){
					                        bool.set(false);
				                        }
				                        throw new RuntimeException("test");
			                        }
		                        })
		                        .retry(3, e -> bool.get()))
		            .verifyErrorMessage("test");
	}

	@Test
	public void scanOperator(){
		MonoRetryPredicate<String> test = new MonoRetryPredicate<>(Mono.just("foo"), e -> true);

	    assertThat(test.scan(Scannable.Attr.RUN_STYLE)).isSameAs(Scannable.Attr.RunStyle.SYNC);
	}
}