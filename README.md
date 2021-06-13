<!--
SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
SPDX-License-Identifier: CC-BY-SA-4.0
-->

# Can

Can is a ChaCha20 cryptographic processor / accelerator based on a No-Instruction-Set Computer (NISC) architecture.

This is a COMP6200 MSc Project.

## Building

```shell
# Run the Scala code to run the Chisel/FIRRTL compiler to generate Verilog
sbt run
```

## Testing

```shell
# Run all test benches
sbt test

# Generate coverage report
sbt coverage test coverageReport
```

### Simulation

To do...

### Synthesis

Chisel compiles to synthesizable Verilog HDL, so any synthesizer supporting Verilog as input can synthesize this project.

To do...

## License

This project is [REUSE 3.0][reuse] compliant. Every file in this repository either contains a comment header or has a corresponding `.license` file identifying the license of the file, so different files may be licensed under different terms. In general:

- Hardware design files are licensed under the [CERN OHL 2.0 - Weakly Reciprocal][CERN-OHL-W-2.0].
- Software source files are licensed under the [GNU GPL v3, or later versions][GPL-3.0-or-later].
- Configuration files are (un)licensed under the [CC0 1.0][CC0-1.0].
- General documentation files are licensed under the [CC-BY-SA 4.0][CC-BY-SA-4.0].

The corresponding full license texts are available under the `LICENSES` folder. In addition, the `LICENSE` file is available for non-REUSE practices.

[reuse]: https://reuse.software/
[CERN-OHL-W-2.0]: https://ohwr.org/project/cernohl/wikis/Documents/CERN-OHL-version-2
[GPL-3.0-or-later]: https://www.gnu.org/licenses/gpl-3.0.html
[CC0-1.0]: https://creativecommons.org/publicdomain/zero/1.0/
[Unlicense]: https://unlicense.org/
[CC-BY-SA-4.0]: https://creativecommons.org/licenses/by-sa/4.0/
