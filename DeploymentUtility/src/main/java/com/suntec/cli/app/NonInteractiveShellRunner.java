package com.suntec.cli.app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.shell.Input;
import org.springframework.shell.InputProvider;
import org.springframework.shell.Shell;

@Order(0)
public class NonInteractiveShellRunner implements ApplicationRunner {

	private final Shell shell;

	public NonInteractiveShellRunner(Shell shell) {
		this.shell = shell;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		shell.run(new PredefinedInputProvider(args.getSourceArgs()));
	}

	public static class PredefinedInputProvider implements InputProvider {

		private final Input input;
		private boolean commandExecuted = false;

		public PredefinedInputProvider(String[] args) {
			this.input = new PredefinedInput(args);
		}

		@Override
		public Input readInput() {
			if (!commandExecuted) {
				commandExecuted = true;
				return input;
			}
			return new PredefinedInput(new String[] { "exit" });
		}

		private static class PredefinedInput implements Input {

			private final String[] args;

			public PredefinedInput(String[] args) {
				this.args = args;
			}

			@Override
			public String rawText() {
				return Stream.of(args).collect(Collectors.joining(" "));
			}

			@Override
			public List<String> words() {
				return Arrays.asList(args);
			}
		}

	}

}
