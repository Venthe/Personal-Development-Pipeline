import {ActionStepDefinition} from '@pipeline/types';
import {shellMany} from '@pipeline/process'
import {callbacks, context, download, RepositoryType, step, untar} from '@pipeline/core';

type With = {
  version?: string,
};

// TODO: Swap pyenv to something faster
//  it takes a minute to configure a Python version
(async function () {
  const _with = (step as ActionStepDefinition<With>)?.with ?? {};
  const version = _with?.version ?? "3.8.0";

  process.env['DEBIAN_FRONTEND'] = 'noninteractive';
  callbacks.addToPath(`${process.env.HOME}/.pyenv/bin`);
  callbacks.addEnv('PYENV_ROOT', `${process.env.HOME}/.pyenv`);

  if (version.startsWith("2")) {
    await shellMany([
      'apt-get remove --assume-yes libssl-dev',
      'apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32',
      'add-apt-repository deb http://security.ubuntu.com/ubuntu bionic-security main',
      'apt-get update',
      'apt-get install --assume-yes libssl1.0-dev'
    ])
  }

  await shellMany([
    `curl --no-progress-meter https://pyenv.run | bash`,
    `pyenv init --path`,
    `pyenv install ${version}`,
    // `pyenv shell ${version}`,
    `pyenv global ${version}`,
    "ln -s `which python2.7` /usr/bin/python"
  ]);

  callbacks.addToPath(`${process.env.HOME}/.pyenv/shims`);
})();
