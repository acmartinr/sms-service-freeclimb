import React, {Component} from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import md5 from 'md5';

import AccountAPI from './AccountAPI';
import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import './Account.css';
import '../common/Session';
import Settings from '../settings/Settings';

import TimezonePicker from 'react-timezone';
import timezones from './TimeZones';

class Account extends Component {
  constructor(props) {
    super(props);

    this.state = {
      errors: {oldPassword: '', newPassword: '', newPasswordRepeated: '', amount: ''},
      oldPassword: '',
      newPassword: '',
      newPasswordRepeated: '',
      passwordChangeLoading: false,
      amount: '',
      stripeInit: false,
      stripeInitFinished: false,
      addPaymentLoading: false,
      changePasswordMessage: '',
      changeTimezoneMessage: '',
      addPaymentMessage: '',
      stripe: {},
      card: {},
      user: Session.getUser(),
      masterIgnoreEnabled: Session.getUser().masterIgnoreEnabled,
      carrierIgnoreEnabled: Session.getUser().carrierIgnoreEnabled,
      chatCarrierLookupEnabled: Session.getUser().chatCarrierLookupEnabled,
      timezoneName: Session.getUser().timezoneName ? Session.getUser().timezoneName : 'America/Chicago',
      timezone: '',
      amountError: false,
      paymentAvailable: false
    };

    this.changePassword = this.changePassword.bind(this);
    this.initStripe = this.initStripe.bind(this);
  }

  componentDidMount() {
    var that = this;

    CampaignsAPI.getUserUISettings({id: Session.getUser().id}, function (response) {
      if (response.status === 'OK') {
        if (response.data) {
          that.setState({
            masterIgnoreEnabled: response.data.find(setting => setting.skey.includes("master.ignore.enabled")).sval,
            carrierIgnoreEnabled: response.data.find(setting => setting.skey.includes("carrier.ignore.enabled")).sval,
            paymentAvailable: response.data.find(setting => setting.skey.includes("paymentAvailable")).sval === "1",
            chatCarrierLookupEnabled: response.data.find(setting => setting.skey.includes("chat.carrier.lookup.enabled")).sval,
          });

          Session.updateUserProperty('masterIgnoreEnabled', response.data.find(setting => setting.skey.includes("master.ignore.enabled")).sval);
          Session.updateUserProperty('carrierIgnoreEnabled', response.data.find(setting => setting.skey.includes("carrier.ignore.enabled")).sval);
          Session.updateUserProperty('chatCarrierLookupEnabled', response.data.find(setting => setting.skey.includes("chat.carrier.lookup.enabled")).sval);
        }
      }
    });
  }

  initStripe() {
    this.setState({stripeInit: true}, function () {
      var stripe = window.Stripe(
        Session.getUser().resellerId !== 214 ?
          'pk_live_ncrIYEMQHORr9fXN7JYgTGNh00Krwml5W0' :
          'pk_live_sKgodW0LStFSbxu7VU8Var3Z');
      this.setState({'stripe': stripe});

      var elements = stripe.elements();

      var style = {
        base: {
          color: '#32325d',
          fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
          fontSmoothing: 'antialiased',
          fontSize: '16px',
          '::placeholder': {
            color: '#aab7c4'
          }
        },
        invalid: {
          color: '#fa755a',
          iconColor: '#fa755a'
        }
      };

      var card = elements.create('card', {style: style});
      this.setState({'card': card});

      card.mount('#card-element');
      this.setState({stripeInitFinished: true});

      card.addEventListener('change', function (event) {
        var displayError = document.getElementById('card-errors');
        if (event.error) {
          displayError.textContent = event.error.message;
        } else {
          displayError.textContent = '';
        }
      });
    });
  }

  paymentAvailable() {
    return Session.getUser().username !== '3109221175' && this.state.paymentAvailable;
  }

  changePassword() {
    var errorsCount = 0;
    var errors = {};

    if (!this.state.oldPassword) {
      errors.oldPassword = 'Please enter old password';
      errorsCount = errorsCount + 1;
    }

    if (!this.state.newPassword) {
      errors.newPassword = 'Please enter new password';
      errorsCount = errorsCount + 1;
    }

    if (!this.state.newPasswordRepeated) {
      errors.newPasswordRepeated = 'Please repeat new password';
      errorsCount = errorsCount + 1;
    }

    if (this.state.newPassword && this.state.newPasswordRepeated && this.state.newPassword !== this.state.newPasswordRepeated) {
      errors.newPasswordRepeated = 'New password and repeated new password should be the same';
      errorsCount = errorsCount + 1;
    }

    this.setState({'errors': errors});
    if (errorsCount === 0) {
      var request = {
        username: Session.getUser().username,
        oldPassword: md5(this.state.oldPassword),
        newPassword: md5(this.state.newPassword)
      };

      this.setState({changePasswordLoading: true});

      var that = this;
      AccountAPI.changePassword(request, function (response) {
        if (response.status === 'OK') {
          that.setState({
            changePasswordLoading: false,
            changePasswordMessage: 'Your password has been changed successfully',
            oldPassword: '',
            newPassword: '',
            newPasswordRepeated: '',
          });
        } else {
          that.setState({changePasswordLoading: false, errors: {oldPassword: 'Entered password is invalid'}});
        }
      });
    }
  }

  addPayment() {
    if (!this.state.amount) {
      this.setState({errors: {amount: 'Enter amount'}});
      return;
    }

    if (this.state.amount < 25) {
      this.setState({amountError: true});
      return;
    }

    this.setState({addPaymentLoading: true, amountError: false});

    var that = this;
    this.state.stripe.createToken(this.state.card).then(function (result) {
      if (result.error) {
        var errorElement = document.getElementById('card-errors');
        errorElement.textContent = result.error.message;

        that.setState({addPaymentLoading: false});
      } else {
        var request = {
          token: result.token.id,
          amount: that.state.amount,
          username: Session.getUser().username
        };

        AccountAPI.addPayment(request, function (response) {
          if (response.status === 'OK') {
            that.setState({
              addPaymentLoading: false,
              addPaymentMessage: 'Your payment has been proceeded successfully. Your balance will be updated in a few seconds.'
            });
          } else {
            that.setState({addPaymentLoading: false, addPaymentMessage: response.message});
          }
        });
      }
    });
  }

  onTextFieldChanged(event, field) {
    if (field === "amount" && !this.state.stripeInit) {
      this.initStripe();
    }

    this.setState({
      changePasswordMessage: '',
      addPaymentMessage: '',
      amountError: false,
      errors: {oldPassword: '', newPassword: '', newPasswordRepeated: '', amount: ''},
      [field]: event.target.value
    });
  }

  formatResellerNumber(number) {
    var result = number + "";

    while (result.length < 3) {
      result = "0" + result;
    }

    return result;
  }

  onTimezoneChanged(timezone) {
    var offset = -6;

    for (var i = 0; i < timezones.length; i++) {
      if (timezones[i].name === timezone) {
        offset = timezones[i].offset;
      }
    }

    this.setState({'timezoneName': timezone, 'timezone': offset});
  }

  changeTimezone() {
    var request = {
      timezoneName: this.state.timezoneName,
      timezoneOffset: this.state.timezone,
      id: Session.getUser().id
    };

    var that = this;
    AccountAPI.updateTimeZone(request, function (response) {
      if (response.status === 'OK') {
        that.setState({
          changeTimezoneMessage: 'Your timezone has been updated successfully'
        });

        Session.updateUserProperty('timezoneName', that.state.timezoneName);
      }
    });
  }

  render() {
    return (
      <div className='max-width-600'>
        <div>
          <span
            className='change-password-title'>{this.state.changeTimezoneMessage ? this.state.changeTimezoneMessage : 'Timezone settings'}</span>
          <TimezonePicker
            className='timezone-picker'
            value={this.state.timezoneName}
            onChange={(timezone) => this.onTimezoneChanged(timezone)}
            inputProps={{
              placeholder: 'Select Timezone...',
              name: 'timezone',
            }}
          />

          <div className='change-password-button-wrapper'>
            <Button className='change-password-button'
                    onClick={(e) => this.changeTimezone()}
                    color="primary">
              Update timezone
            </Button>
          </div>
          <div className='account-spacer'/>
        </div>

        {this.state.user.role === 3 &&
        <div>
          <span className='change-password-title'>Reseller invitation link</span>
          <span
            className='margin-top-10'>https://textalldata.com/{this.formatResellerNumber(this.state.user.resellerNumber)}</span>
          <div className='account-spacer'/>
        </div>}

        <span
          className='change-password-title'>{this.state.changePasswordMessage ? this.state.changePasswordMessage : 'Change password'}</span>
        <TextField
          error={!!this.state.errors.oldPassword}
          margin="dense"
          id="oldPassword"
          label={this.state.errors.oldPassword ? this.state.errors.oldPassword : "Enter old password"}
          type="password"
          value={this.state.oldPassword}
          onChange={(e) => this.onTextFieldChanged(e, "oldPassword")}
          fullWidth
          autoFocus
        />
        <TextField
          error={!!this.state.errors.newPassword}
          margin="dense"
          id="newPassword"
          label={this.state.errors.newPassword ? this.state.errors.newPassword : "Enter new password"}
          type="password"
          value={this.state.newPassword}
          onChange={(e) => this.onTextFieldChanged(e, "newPassword")}
          fullWidth
        />
        <TextField
          error={!!this.state.errors.newPasswordRepeated}
          margin="dense"
          id="newPasswordRepeated"
          label={this.state.errors.newPasswordRepeated ? this.state.errors.newPasswordRepeated : "Repeat new password"}
          type="password"
          value={this.state.newPasswordRepeated}
          onChange={(e) => this.onTextFieldChanged(e, "newPasswordRepeated")}
          fullWidth
        />
        <div className='change-password-button-wrapper'>
          <Button className='change-password-button'
                  disabled={this.state.changePasswordLoading}
                  onClick={(e) => this.changePassword()}
                  color="primary">
            Change password
          </Button>
        </div>

        <div className='account-spacer'/>

        {this.paymentAvailable() && <React.Fragment>
          <span
            className='change-password-title'>{this.state.addPaymentMessage ? this.state.addPaymentMessage : 'Add payment'}</span>
          <TextField
            error={!!this.state.errors.amount}
            margin="dense"
            id="amount"
            className="account-amount-input"
            label={this.state.errors.amount ? this.state.errors.amount : "Enter amount"}
            type="number"
            value={this.state.amount}
            onChange={(e) => this.onTextFieldChanged(e, "amount")}
          />
          {this.state.stripeInit && <div className='account-card-wrapper'>
            <div id="card-element">
            </div>

            <div id="card-errors" role="alert" className='payment-error-message'></div>
          </div>}
          {this.state.stripeInitFinished &&
          <Button disabled={this.state.addPaymentLoading}
                  className='account-payment-button'
                  onClick={(e) => this.addPayment()}
                  color="primary">
            Add payment
          </Button>
          }
          {this.state.amountError && <span className='amount-error'>Your payment amount should be more than $25</span>}
        </React.Fragment>}

        {this.state.masterIgnoreEnabled === '1' &&
        <div className='account-settings-table'>
          <div className='account-spacer'/>
          <span className='change-password-title'>Settings</span>
          <Settings type='master_ignore'/>
        </div>
        }

        {this.state.chatCarrierLookupEnabled === '1' &&
        <div className='account-settings-table'>
          <div className='account-spacer'/>
          <span className='change-password-title'>Carrier lookup settings</span>
          <Settings type='chat_carrier_lookup'/>
        </div>
        }

        {this.state.carrierIgnoreEnabled === '1' &&
        <div className='account-settings-table'>
          <div className='account-spacer'/>
          <span className='change-password-title'>Ignore carriers</span>
          <Settings type='carrier_ignore'/>
        </div>
        }
      </div>
    )
  }
}

export default Account;

