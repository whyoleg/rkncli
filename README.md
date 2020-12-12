# rkncli

RSocket CLI implemented in Kotlin/Native. Works only with TCP transport.

Repository also contains `proxy` module, which can redirect TCP requests to WS route, provided in setup payload.

## Building

To build CLI run gradle task `:cli:linkReleaseExecutableNative`. It will create executable on
path `cli/build/bin/native/releaseExecutable/cli.kexe`. After that just call commands.

Example:

```
./cli.kexe rs 0.0.0.0 9000 \
        --setupData wss://rsocket-demo.herokuapp.com/rsocket \
        --route searchTweets \
        --data Sunday
```

To start proxy, just run `rkncli.proxy.main` function. It will start TCP server on `0.0.0.0:9000` that will connect through WS to `url`
provided in setup payload data.
