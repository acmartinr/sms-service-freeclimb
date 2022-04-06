import React, {Component} from 'react';

import './RegistrationModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';
import CircularProgress from '@material-ui/core/CircularProgress';

import LoginAPI from './LoginAPI';
import md5 from 'md5';

class RegistrationModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      phone: '',
      phoneError: false,
      phoneUniqueError: false,
      phoneSentError: '',
      fullName: '',
      fullNameError: false,
      code: '',
      codeError: false,
      codeValueError: false,
      password: '',
      passwordError: false,
      confirmPassword: '',
      confirmPasswordError: false,
      phase: 0,
      submitButtonTitle: 'Send PIN code',
      loading: false
    };
  }

  onTextFieldChanged(event, field) {
    this.setState({
      phoneError: false,
      codeError: false,
      passwordError: false,
      confirmPasswordError: false,
      fullNameError: false,
      [field]: event.target.value
    });
  }

  submit() {
    if (this.state.phase === 0) {
      this.sendPinCode();
    } else {
      this.register();
    }
  }

  register() {
    this.setState({codeValueError: false});

    if (!this.state.password) {
      this.setState({passwordError: true});
    }

    if (!this.state.confirmPassword) {
      this.setState({confirmPasswordError: true});
    }

    if (!this.state.code || isNaN(this.state.code) || !this.state.code.length <= 5) {
      this.setState({codeError: true});
    }

    if (!this.state.fullName) {
      this.setState({fullNameError: true});
    }

    if (this.state.password !== this.state.confirmPassword) {
      this.setState({confirmPasswordError: true});
    }

    if (this.state.confirmPassword && this.state.password &&
      this.state.code && !isNaN(this.state.code) && this.state.code.length <= 5 && this.state.password === this.state.confirmPassword) {
      this.setState({loading: true});

      var resellerNumber;

      var urlParts = document.URL.split("/")
      var lastPart = urlParts[urlParts.length - 1]
      if (lastPart !== "login") {
        resellerNumber = lastPart;
      }

      const request = {
        phone: this.state.phone,
        code: this.state.code,
        password: md5(this.state.password),
        fullName: this.state.fullName,
        'resellerNumber': resellerNumber
      };

      LoginAPI.register(request,
        response => {
          this.setState({loading: false});

          if (response.status === 'OK') {
            this.props.handleClose(request.phone, request.password);
          } else {
            if (response.message === 'code') {
              this.setState({codeValueError: true});
            } else {
              this.setState({phoneSentError: response.message});
            }
          }
        });
    }
  }

  sendPinCode() {
    this.setState({phoneUniqueError: false, phoneSentError: false});

    if (!this.state.phone.length) {
      this.setState({phoneError: true});
      return;
    }

    this.setState({loading: true});
    const request = {phone: this.state.phone};

    LoginAPI.sendPinCode(request,
      response => {
        this.setState({loading: false});

        if (response.status === 'OK') {
          this.setState({phase: 1, submitButtonTitle: 'Sign Up'})
        } else {
          this.setState({phase: 1, submitButtonTitle: 'Sign Up'})
          if (response.message === 'phone') {
            this.setState({phoneUniqueError: true});
          } else {
            this.setState({phoneSentError: response.message});
          }
        }
      });
  }

  render() {
    return (
      <Dialog
        open
        onClose={(e) => this.props.handleClose()}
        aria-labelledby="form-dialog-title"
        className="custom-registration-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Registration</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            error={this.state.phoneError || this.state.phoneUniqueError || this.state.phoneSentError.length > 0}
            margin="dense"
            id="phone"
            label="Enter your phone"
            type="text"
            value={this.state.phone}
            onChange={(e) => this.onTextFieldChanged(e, "phone")}
            fullWidth
          />

          {this.state.phoneUniqueError &&
          <Typography color="error" component="h5" variant="subtitle1">
            An user with entered phone number is already exists
          </Typography>
          }

          {this.state.phoneSentError.length > 0 &&
          <Typography color="error" component="h5" variant="subtitle1">
            {this.state.phoneSentError}
          </Typography>
          }

          {this.state.phase === 1 &&
          <div>

            <TextField
              error={this.state.codeError || this.state.codeValueError}
              margin="dense"
              id="code"
              label="Enter PIN code from SMS"
              type="text"
              value={this.state.code}
              onChange={(e) => this.onTextFieldChanged(e, "code")}
              fullWidth
            />

            {this.state.codeValueError &&
            <Typography color="error" component="h5" variant="subtitle1">
              Entered PIN code is not correct. Please try again.
            </Typography>
            }

            <TextField
              error={this.state.fullNameError}
              margin="dense"
              id="code"
              label="Enter your business name"
              type="text"
              value={this.state.fullName}
              onChange={(e) => this.onTextFieldChanged(e, "fullName")}
              fullWidth
            />

            <TextField
              error={this.state.passwordError}
              margin="normal"
              fullWidth
              name="password"
              label="Enter your password"
              type="password"
              id="password"
              onChange={(e) => this.onTextFieldChanged(e, "password")}/>

            <TextField
              error={this.state.confirmPasswordError}
              fullWidth
              name="confirmPassword"
              label="Repeat your password"
              type="password"
              id="confirmPassword"
              onChange={(e) => this.onTextFieldChanged(e, "confirmPassword")}/>

            {this.state.confirmPasswordError &&
            <Typography color="error" component="h5" variant="subtitle1">
              Password and repeated password should be the same.
            </Typography>
            }

          </div>
          }

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.submit()} color="primary" disabled={this.state.loading}>
            {this.state.submitButtonTitle}
          </Button>

          {this.state.loading &&
          <CircularProgress className="custom-progress-registration-details"/>
          }
        </DialogActions>
      </Dialog>
    )
  }
}

export default RegistrationModal;
